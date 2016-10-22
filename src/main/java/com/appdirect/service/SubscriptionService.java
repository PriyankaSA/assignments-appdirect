package com.appdirect.service;

import com.appdirect.mongo.documents.SubscriptionRecord;
import com.appdirect.web.commons.Response;

import java.util.List;

public interface SubscriptionService {
    Response createSubscription(String url);

    Response changeSubscription(String url);

    List<SubscriptionRecord> getSubscriptions();

    Response cancelSubscription(String url);
}
