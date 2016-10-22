package com.appdirect.service;

import com.appdirect.client.AppDirectClient;
import com.appdirect.dto.Account;
import com.appdirect.dto.Company;
import com.appdirect.dto.User;
import com.appdirect.dto.subscription.*;
import com.appdirect.mongo.SubscriptionRecordRepository;
import com.appdirect.mongo.UserRecordRepository;
import com.appdirect.mongo.documents.SubscriptionRecord;
import com.appdirect.mongo.documents.UserRecord;
import com.appdirect.web.commons.ErrorResponse;
import com.appdirect.web.commons.Response;
import com.appdirect.web.commons.SuccessResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SubscriptionServiceTest {
    @Mock
    private UserRecordRepository userRecordRepository;

    @Mock
    private SubscriptionRecordRepository subscriptionRecordRepository;

    @Mock
    private AppDirectClient appDirectClient;

    @InjectMocks
    private SubscriptionService subscriptionService = new SubscriptionServiceImpl();

    private static final String url = "www.abc.com";
    private static final String accountId = "123";
    private static final String userCreatorId = "1";
    private static final String userId = "2";
    private static final String exMsg = "Fake ex";

    @Test
    public void testCreateSubscriptionWhenThereIsException(){
        when(appDirectClient.getNotification(url, Notification.class)).thenThrow(new RestClientException(exMsg));

        Response response = subscriptionService.createSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.UNKNOWN_ERROR, errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("Exception thrown"));
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testCreateSubscriptionWhenNotificationEventIsNull(){
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(null);

        Response response = subscriptionService.createSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event returned from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testCreateSubscriptionWhenNotificationIsNotSubscriptionOrder(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_ASSIGNMENT);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);

        Response response = subscriptionService.createSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event type from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testCreateSubscriptionWhenUserAlreadyExists(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.SUBSCRIPTION_ORDER);
        User creator = new User();
        creator.setOpenId(userCreatorId);
        notification.setCreator(creator);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(userRecordRepository.findByOpenId(userCreatorId)).thenReturn(new UserRecord());

        Response response = subscriptionService.createSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.USER_ALREADY_EXISTS, errorResponse.getErrorCode());
        assertEquals("User already exists", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(userRecordRepository).findByOpenId(userCreatorId);
    }

    @Test
    public void testCreateSubscription(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.SUBSCRIPTION_ORDER);
        User creator = new User();
        creator.setOpenId(userCreatorId);
        notification.setCreator(creator);
        Payload payload = new Payload();
        Order order = new Order();
        order.setPricingDuration(PricingDuration.WEEKLY);
        payload.setOrder(order);
        payload.setCompany(new Company());
        notification.setPayload(payload);
        notification.setMarketplace(new MarketPlace());
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(userRecordRepository.findByOpenId(userCreatorId)).thenReturn(null);
        when(subscriptionRecordRepository.save(any(SubscriptionRecord.class))).thenReturn(new SubscriptionRecord());
        when(userRecordRepository.save(any(UserRecord.class))).thenReturn(new UserRecord());

        Response response = subscriptionService.createSubscription(url);

        assertTrue(response instanceof SuccessResponse);
        SuccessResponse successResponse = (SuccessResponse) response;
        assertTrue(successResponse.getSuccess());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(userRecordRepository).findByOpenId(userCreatorId);
        ArgumentCaptor<SubscriptionRecord> subscriptionCaptor = ArgumentCaptor.forClass(SubscriptionRecord.class);
        verify(subscriptionRecordRepository).save(subscriptionCaptor.capture());
        assertEquals(PricingDuration.WEEKLY, subscriptionCaptor.getValue().getPricingDuration());
        ArgumentCaptor<UserRecord> userCaptor = ArgumentCaptor.forClass(UserRecord.class);
        verify(userRecordRepository).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().isOwner());
    }

    @Test
    public void testChangeSubscriptionWhenThereIsException(){
        when(appDirectClient.getNotification(url, Notification.class)).thenThrow(new RestClientException(exMsg));

        Response response = subscriptionService.changeSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.UNKNOWN_ERROR, errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("Exception thrown"));
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testChangeSubscriptionWhenNotificationEventIsNull(){
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(null);

        Response response = subscriptionService.changeSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event returned from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testChangeSubscriptionWhenNotificationIsNotSubscriptionOrder(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_ASSIGNMENT);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);

        Response response = subscriptionService.changeSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event type from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testChangeSubscriptionWhenSubscriptionAccountNotFound(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.SUBSCRIPTION_CHANGE);
        User creator = new User();
        creator.setOpenId(userCreatorId);
        notification.setCreator(creator);
        Account account = new Account();
        account.setAccountId(accountId);
        Payload payload = new Payload();
        payload.setAccount(account);
        notification.setPayload(payload);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(null);

        Response response = subscriptionService.changeSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, errorResponse.getErrorCode());
        assertEquals("Subscription Account Not Found", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
    }

    @Test
    public void testChangeSubscription(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.SUBSCRIPTION_CHANGE);
        User creator = new User();
        creator.setOpenId(userCreatorId);
        notification.setCreator(creator);
        Payload payload = new Payload();
        Order order = new Order();
        order.setPricingDuration(PricingDuration.WEEKLY);
        payload.setOrder(order);
        payload.setCompany(new Company());
        notification.setPayload(payload);
        notification.setMarketplace(new MarketPlace());
        Account account = new Account();
        account.setAccountId(accountId);
        account.setStatus(Account.Status.ACTIVE);
        payload.setAccount(account);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        SubscriptionRecord subscriptionRecord = new SubscriptionRecord();
        subscriptionRecord.setPricingDuration(PricingDuration.DAILY);
        subscriptionRecord.setStatus(Account.Status.INACTIVE.toString());
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(subscriptionRecord);

        Response response = subscriptionService.changeSubscription(url);

        assertTrue(response instanceof SuccessResponse);
        SuccessResponse successResponse = (SuccessResponse) response;
        assertTrue(successResponse.getSuccess());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
        ArgumentCaptor<SubscriptionRecord> subscriptionCaptor = ArgumentCaptor.forClass(SubscriptionRecord.class);
        verify(subscriptionRecordRepository).save(subscriptionCaptor.capture());
        assertEquals(PricingDuration.WEEKLY, subscriptionCaptor.getValue().getPricingDuration());
        assertEquals(Account.Status.ACTIVE.toString(), subscriptionCaptor.getValue().getStatus());
    }

    @Test
    public void testCancelSubscriptionWhenThereIsException(){
        when(appDirectClient.getNotification(url, Notification.class)).thenThrow(new RestClientException(exMsg));

        Response response = subscriptionService.cancelSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.UNKNOWN_ERROR, errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("Exception thrown"));
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testCancelSubscriptionWhenNotificationEventIsNull(){
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(null);

        Response response = subscriptionService.cancelSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event returned from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testCancelSubscriptionWhenNotificationIsNotSubscriptionOrder(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.USER_ASSIGNMENT);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);

        Response response = subscriptionService.cancelSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.INVALID_RESPONSE, errorResponse.getErrorCode());
        assertEquals("Invalid event type from appdirect", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
    }

    @Test
    public void testCancelSubscriptionWhenSubscriptionAccountNotFound(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.SUBSCRIPTION_CANCEL);
        User creator = new User();
        creator.setOpenId(userCreatorId);
        notification.setCreator(creator);
        Account account = new Account();
        account.setAccountId(accountId);
        Payload payload = new Payload();
        payload.setAccount(account);
        notification.setPayload(payload);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(null);

        Response response = subscriptionService.cancelSubscription(url);

        assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, errorResponse.getErrorCode());
        assertEquals("Subscription Account Not Found", errorResponse.getMessage());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
    }

    @Test
    public void testCancelSubscription(){
        Notification notification = new Notification();
        notification.setType(Notification.Type.SUBSCRIPTION_CANCEL);
        User creator = new User();
        creator.setOpenId(userCreatorId);
        notification.setCreator(creator);
        Payload payload = new Payload();
        Order order = new Order();
        order.setPricingDuration(PricingDuration.WEEKLY);
        payload.setOrder(order);
        payload.setCompany(new Company());
        notification.setPayload(payload);
        Account account = new Account();
        account.setAccountId(accountId);
        payload.setAccount(account);
        when(appDirectClient.getNotification(url, Notification.class)).thenReturn(notification);
        SubscriptionRecord subscriptionRecord = new SubscriptionRecord();
        subscriptionRecord.setId(accountId);
        when(subscriptionRecordRepository.findOne(accountId)).thenReturn(subscriptionRecord);
        doNothing().when(subscriptionRecordRepository).delete(subscriptionRecord);
        doNothing().when(userRecordRepository).deleteBySubscriptionId(accountId);

        Response response = subscriptionService.cancelSubscription(url);

        assertTrue(response instanceof SuccessResponse);
        SuccessResponse successResponse = (SuccessResponse) response;
        assertTrue(successResponse.getSuccess());
        verify(appDirectClient).getNotification(url, Notification.class);
        verify(subscriptionRecordRepository).findOne(accountId);
        verify(subscriptionRecordRepository).delete(subscriptionRecord);
        verify(userRecordRepository).deleteBySubscriptionId(accountId);
    }


}
