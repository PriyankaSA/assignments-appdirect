package com.appdirect.service;


import com.appdirect.mongo.documents.UserRecord;
import com.appdirect.web.commons.Response;

import java.util.List;

public interface UserService {

    Response assignUser(String url);

    Response unassignUser(String url);

    List<UserRecord> findUsers(String subscriptionId);
}
