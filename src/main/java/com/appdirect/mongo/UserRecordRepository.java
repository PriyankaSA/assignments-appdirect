package com.appdirect.mongo;

import com.appdirect.mongo.documents.UserRecord;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRecordRepository extends CrudRepository<UserRecord, String> {
    UserRecord findByOpenId(String openId);

    List<UserRecord> findBySubscriptionId(String subscriptionId);

    @Query(value = "{'openId' : ?0 , 'subscriptionId': ?1 , 'isOwner': true }")
    UserRecord userOwner(String openId, String subscriptionId);

    @Query(value = "{'subscriptionId': ?0 }", delete = true)
    void deleteBySubscriptionId(String subscriptionId);
}
