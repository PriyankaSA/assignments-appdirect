package com.appdirect.service;

import com.appdirect.client.AppDirectClient;
import com.appdirect.dto.User;
import com.appdirect.dto.subscription.Notification;
import com.appdirect.mongo.SubscriptionRecordRepository;
import com.appdirect.mongo.UserRecordRepository;
import com.appdirect.mongo.documents.SubscriptionRecord;
import com.appdirect.mongo.documents.UserRecord;
import com.appdirect.web.commons.ErrorResponse;
import com.appdirect.web.commons.Response;
import com.appdirect.web.commons.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceImpl implements UserService {
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserRecordRepository userRecordRepository;

    @Resource
    private SubscriptionRecordRepository subscriptionRecordRepository;

    @Resource
    private AppDirectClient appDirectClient;

    @Override
    public Response assignUser(String url) {
        try {
            final Notification notification = appDirectClient.getNotification(url, Notification.class);

            if(notification == null){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event returned from appdirect");
            }
            if(!Notification.Type.USER_ASSIGNMENT.equals(notification.getType())){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event type from appdirect");
            }

            final String subscriptionId = notification.getPayload().getAccount().getAccountId();
            SubscriptionRecord subscriptionRecord = subscriptionRecordRepository.findOne(subscriptionId);
            if (subscriptionRecord == null) {
                return new ErrorResponse(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, "Subscription Account Not Found");
            }
            //check if the owner is correct
            final String ownerId = notification.getCreator().getOpenId();
            UserRecord ownerRecord = userRecordRepository.userOwner(ownerId, subscriptionId);
            if (ownerRecord == null) {
                return new ErrorResponse(ErrorResponse.ErrorCode.USER_NOT_FOUND, "User is not admin of the subscription");
            }

            //assign user
            final String userId = notification.getPayload().getUser().getOpenId();
            UserRecord userRecord = userRecordRepository.findByOpenId(userId);
            if(userRecord == null){
                userRecord = createUserRecord(notification.getPayload().getUser());
            }
            userRecord.setSubscriptionId(subscriptionId);
            userRecord.setOwner(false);
            userRecordRepository.save(userRecord);

            return new SuccessResponse(userRecord.getOpenId());
        } catch (Exception e) {
            logger.error("Exception thrown", e);
            return new ErrorResponse(ErrorResponse.ErrorCode.UNKNOWN_ERROR, String.format("Exception thrown %s", e.getMessage()));
        }
    }

    private UserRecord createUserRecord(final User user){
        UserRecord userRecord = new UserRecord();
        userRecord.setFirstName(user.getFirstName());
        userRecord.setLastName(user.getLastName());
        userRecord.setEmailId(user.getEmail());
        userRecord.setOpenId(user.getOpenId());
        return userRecord;
    }

    @Override
    public Response unassignUser(String url) {
        try {
            final Notification notification = appDirectClient.getNotification(url, Notification.class);

            if(notification == null){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event returned from appdirect");
            }
            if(!Notification.Type.USER_UNASSIGNMENT.equals(notification.getType())){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event type from appdirect");
            }

            final String subscriptionId = notification.getPayload().getAccount().getAccountId();
            SubscriptionRecord subscriptionRecord = subscriptionRecordRepository.findOne(subscriptionId);
            if (subscriptionRecord == null) {
                return new ErrorResponse(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, "Subscription Account Not Found");
            }
            //check if the owner is correct
            final String ownerId = notification.getCreator().getOpenId();
            UserRecord ownerRecord = userRecordRepository.userOwner(ownerId, subscriptionId);
            if (ownerRecord == null) {
                return new ErrorResponse(ErrorResponse.ErrorCode.USER_NOT_FOUND, "User is not admin of the subscription");
            }

            //unassign user
            final String userId = notification.getPayload().getUser().getOpenId();
            UserRecord userRecord = userRecordRepository.findByOpenId(userId);
            if(userRecord == null || !userRecord.getSubscriptionId().equals(subscriptionId)){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_REQUEST, "Invalid user for the subscription");
            }
            userRecordRepository.delete(userRecord);
            return new SuccessResponse(userRecord.getOpenId());
        } catch (Exception e) {
            logger.error("Exception thrown", e);
            return new ErrorResponse(ErrorResponse.ErrorCode.UNKNOWN_ERROR, String.format("Exception thrown %s", e.getMessage()));
        }
    }

    @Override
    public List<UserRecord> findUsers(String subscriptionId) {
        return userRecordRepository.findBySubscriptionId(subscriptionId);
    }
}
