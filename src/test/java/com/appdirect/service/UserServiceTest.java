package com.appdirect.service;

import com.appdirect.client.AppDirectClient;
import com.appdirect.dto.Account;
import com.appdirect.dto.User;
import com.appdirect.dto.subscription.Notification;
import com.appdirect.dto.subscription.Payload;
import com.appdirect.mongo.SubscriptionRecordRepository;
import com.appdirect.mongo.UserRecordRepository;
import com.appdirect.mongo.documents.SubscriptionRecord;
import com.appdirect.mongo.documents.UserRecord;
import com.appdirect.web.commons.ErrorResponse;
import com.appdirect.web.commons.Response;
import com.appdirect.web.commons.SuccessResponse;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {
    @Mock
    private UserRecordRepository userRecordRepository;

    @Mock
    private SubscriptionRecordRepository subscriptionRecordRepository;

    @Mock
    private AppDirectClient appDirectClient;

    @InjectMocks
    private UserService userService = new UserServiceImpl();

    private static final String url = "www.abc.com";
    private static final String accountId = "123";
    private static final String userCreatorId = "1";
    private static final String userId = "2";
    private static final String exMsg = "Fake ex";

    @Test
    public void testAssignUserWhenThereIsException(){
        when(appDirectClient.getNotification(url, Notification.class)).thenThrow(new RestClientException(exMsg));

        Response response = userService.assignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.UNKNOWN_ERROR, errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("Exception thrown"));
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testAssignUserWhenNotificationEventIsNull(){
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(null);

        Response response = userService.assignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event returned from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testAssignUserWhenNotificationIsNotUSerAssignment(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.SUBSCRIPTION_ORDER);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);

        Response response = userService.assignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event type from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testAssignUserWhenSubscriptionAccountNotFound(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_ASSIGNMENT);
        Account account = new Account();
        account.setAccountId(accountId);
        Payload payload = new Payload();
        payload.setAccount(account);
        notification.setPayload(payload);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(null);

        Response response = userService.assignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, errorResponse.getErrorCode());
        assertEquals("Subscription Account Not Found", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
    }

    @Test
    public void testAssignUserWhenOwnerIsNotCreatorOfSubscription(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_ASSIGNMENT);
        Account account = new Account();
        account.setAccountId(accountId);
        Payload payload = new Payload();
        payload.setAccount(account);
        notification.setPayload(payload);
        User user = new User();
        user.setOpenId(userCreatorId);
        notification.setCreator(user);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(new SubscriptionRecord());
        when(userRecordRepository.userOwner(userCreatorId, accountId)).thenReturn(null);

        Response response = userService.assignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.USER_NOT_FOUND, errorResponse.getErrorCode());
        assertEquals("User is not admin of the subscription", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
        verify(userRecordRepository).userOwner(userCreatorId, accountId);
    }

    @Test
    public void testAssignUser(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_ASSIGNMENT);
        Account account = new Account();
        account.setAccountId(accountId);
        Payload payload = new Payload();
        payload.setAccount(account);
        notification.setPayload(payload);
        User creator = new User();
        creator.setOpenId(userCreatorId);
        notification.setCreator(creator);
        User user = new User();
        user.setOpenId(userId);
        payload.setUser(user);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(new SubscriptionRecord());
        when(userRecordRepository.userOwner(userCreatorId, accountId)).thenReturn(new UserRecord());
        when(userRecordRepository.findByOpenId(userId)).thenReturn(null);
        when(userRecordRepository.save(any(UserRecord.class))).thenReturn(new UserRecord());

        Response response = userService.assignUser(url);

        assertTrue(response instanceof SuccessResponse);
        SuccessResponse successResponse = (SuccessResponse) response;
        assertEquals(userId, successResponse.getAccountIdentifier());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
        verify(userRecordRepository).userOwner(userCreatorId, accountId);
        verify(userRecordRepository).findByOpenId(userId);
        ArgumentCaptor<UserRecord> captor = ArgumentCaptor.forClass(UserRecord.class);
        verify(userRecordRepository).save(captor.capture());
        assertEquals(accountId, captor.getValue().getSubscriptionId());
        assertFalse(captor.getValue().isOwner());
    }

    @Test
    public void testUnassignUserWhenThereIsException(){
        when(appDirectClient.getNotification(url, Notification.class)).thenThrow(new RestClientException(exMsg));

        Response response = userService.unassignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.UNKNOWN_ERROR, errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("Exception thrown"));
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testUnassignUserWhenNotificationEventIsNull(){
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(null);

        Response response = userService.unassignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event returned from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testUnassignUserWhenNotificationIsNotUserAssignment(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.SUBSCRIPTION_ORDER);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);

        Response response = userService.unassignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event type from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testUnassignUserWhenSubscriptionAccountNotFound(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_UNASSIGNMENT);
        Account account = new Account();
        account.setAccountId(accountId);
        Payload payload = new Payload();
        payload.setAccount(account);
        notification.setPayload(payload);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(null);

        Response response = userService.unassignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, errorResponse.getErrorCode());
        assertEquals("Subscription Account Not Found", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
    }

    @Test
    public void testUnassignUserWhenOwnerIsNotCreatorOfSubscription(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_UNASSIGNMENT);
        Account account = new Account();
        account.setAccountId(accountId);
        Payload payload = new Payload();
        payload.setAccount(account);
        notification.setPayload(payload);
        User user = new User();
        user.setOpenId(userCreatorId);
        notification.setCreator(user);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(new SubscriptionRecord());
        when(userRecordRepository.userOwner(userCreatorId, accountId)).thenReturn(null);

        Response response = userService.unassignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.USER_NOT_FOUND, errorResponse.getErrorCode());
        assertEquals("User is not admin of the subscription", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
        verify(userRecordRepository).userOwner(userCreatorId, accountId);
    }

    @Test
    public void testUnassignUserForInvalidUser(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_UNASSIGNMENT);
        Account account = new Account();
        account.setAccountId(accountId);
        Payload payload = new Payload();
        payload.setAccount(account);
        notification.setPayload(payload);
        User creator = new User();
        creator.setOpenId(userCreatorId);
        notification.setCreator(creator);
        User user = new User();
        user.setOpenId(userId);
        payload.setUser(user);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(new SubscriptionRecord());
        when(userRecordRepository.userOwner(userCreatorId, accountId)).thenReturn(new UserRecord());
        UserRecord userRecord = new UserRecord();
        userRecord.setSubscriptionId("1");
        when(userRecordRepository.findByOpenId(userId)).thenReturn(null);

        Response response = userService.unassignUser(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_REQUEST, errorResponse.getErrorCode());
        assertEquals("Invalid user for the subscription", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
        verify(userRecordRepository).userOwner(userCreatorId, accountId);
        verify(userRecordRepository).findByOpenId(userId);
    }

    @Test
    public void testUnassignUser(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_UNASSIGNMENT);
        Account account = new Account();
        account.setAccountId(accountId);
        Payload payload = new Payload();
        payload.setAccount(account);
        notification.setPayload(payload);
        User creator = new User();
        creator.setOpenId(userCreatorId);
        notification.setCreator(creator);
        User user = new User();
        user.setOpenId(userId);
        payload.setUser(user);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(new SubscriptionRecord());
        when(userRecordRepository.userOwner(userCreatorId, accountId)).thenReturn(new UserRecord());
        UserRecord userRecord = new UserRecord();
        userRecord.setSubscriptionId(accountId);
        userRecord.setOpenId(userId);
        when(userRecordRepository.findByOpenId(userId)).thenReturn(userRecord);
        doNothing().when(userRecordRepository).delete(any(UserRecord.class));

        Response response = userService.unassignUser(url);

        assertTrue(response instanceof SuccessResponse);
        SuccessResponse successResponse = (SuccessResponse) response;
        assertEquals(userRecord.getOpenId(), successResponse.getAccountIdentifier());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
        verify(userRecordRepository).userOwner(userCreatorId, accountId);
        verify(userRecordRepository).findByOpenId(userId);
        ArgumentCaptor<UserRecord> captor = ArgumentCaptor.forClass(UserRecord.class);
        verify(userRecordRepository).delete(captor.capture());
        assertEquals(accountId, captor.getValue().getSubscriptionId());

    }
}
