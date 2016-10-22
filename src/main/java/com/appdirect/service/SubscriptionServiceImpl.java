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

/**
 * Created by sardap on 20/10/16.
 */
@Component
public class SubscriptionServiceImpl implements SubscriptionService {
    private Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    @Resource
    private UserRecordRepository userRecordRepository;

    @Resource
    private SubscriptionRecordRepository subscriptionRecordRepository;

    @Resource
    private AppDirectClient appDirectClient;

    @Override
    public Response createSubscription(String url) {
        try {
            final Notification notification = appDirectClient.getNotification(url, Notification.class);

            if(notification == null){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event returned from appdirect");
            }
            if(!Notification.Type.SUBSCRIPTION_ORDER.equals(notification.getType())){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event type from appdirect");
            }

            if (userRecordRepository.findByOpenId(notification.getCreator().getOpenId()) != null) {
                return new ErrorResponse(ErrorResponse.ErrorCode.USER_ALREADY_EXISTS, "User already exists");
            }

            SubscriptionRecord subscriptionRecord = createSubscriptionRecord(notification);
            subscriptionRecordRepository.save(subscriptionRecord);

            UserRecord userRecord = createUserRecord(notification.getCreator());
            userRecord.setSubscriptionId(subscriptionRecord.getId());
            userRecord.setOwner(true);
            userRecordRepository.save(userRecord);

            return new SuccessResponse(subscriptionRecord.getId());
        } catch (Exception e) {
            logger.error("Exception thrown", e);
            return new ErrorResponse(ErrorResponse.ErrorCode.UNKNOWN_ERROR, String.format("Exception thrown %s", e.getMessage()));
        }
    }

    @Override
    public Response changeSubscription(String url) {
        try {
            final Notification notification = appDirectClient.getNotification(url, Notification.class);

            if(notification == null){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event returned from appdirect");
            }
            if(!Notification.Type.SUBSCRIPTION_CHANGE.equals(notification.getType())){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event type from appdirect");
            }

            final String subscriptionId = notification.getPayload().getAccount().getAccountId();
            SubscriptionRecord subscriptionRecord = subscriptionRecordRepository.findOne(subscriptionId);
            if (subscriptionRecord == null) {
                return new ErrorResponse(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, "Subscription Account Not Found");
            }
            subscriptionRecord.setEdition(notification.getPayload().getOrder().getEditionCode());
            subscriptionRecord.setPricingDuration(notification.getPayload().getOrder().getPricingDuration());
            subscriptionRecord.setStatus(notification.getPayload().getAccount().getStatus().toString());

            subscriptionRecordRepository.save(subscriptionRecord);

            return new SuccessResponse(subscriptionRecord.getId());
        } catch (Exception e) {
            logger.error("Exception thrown", e);
            return new ErrorResponse(ErrorResponse.ErrorCode.UNKNOWN_ERROR, String.format("Exception thrown %s", e.getMessage()));
        }
    }

    @Override
    public List<SubscriptionRecord> getSubscriptions() {
        List<SubscriptionRecord> records = new ArrayList<>();
        subscriptionRecordRepository.findAll().forEach(record -> records.add(record));
        return records;
    }

    @Override
    public Response cancelSubscription(String url) {
        try {
            final Notification notification = appDirectClient.getNotification(url, Notification.class);

            if(notification == null){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event returned from appdirect");
            }
            if(!Notification.Type.SUBSCRIPTION_CANCEL.equals(notification.getType())){
                return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_RESPONSE, "Invalid event type from appdirect");
            }

            final String subscriptionId = notification.getPayload().getAccount().getAccountId();
            SubscriptionRecord subscriptionRecord = subscriptionRecordRepository.findOne(subscriptionId);
            if (subscriptionRecord == null) {
                return new ErrorResponse(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, "Subscription Account Not Found");
            }
            subscriptionRecordRepository.delete(subscriptionRecord);
            userRecordRepository.deleteBySubscriptionId(subscriptionId);

            return new SuccessResponse(subscriptionRecord.getId());
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

    private SubscriptionRecord createSubscriptionRecord(final Notification notification){
        SubscriptionRecord record = new SubscriptionRecord();
        record.setCompanyName(notification.getPayload().getCompany().getName());
        record.setCompanyUuid(notification.getPayload().getCompany().getUuid());
        record.setEdition(notification.getPayload().getOrder().getEditionCode());
        record.setPricingDuration(notification.getPayload().getOrder().getPricingDuration());
        record.setMarketPlaceBaseUrl(notification.getMarketplace().getBaseUrl());
        return record;
    }
}
