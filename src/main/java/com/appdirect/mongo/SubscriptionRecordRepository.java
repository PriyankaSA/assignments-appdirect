package com.appdirect.mongo;

import com.appdirect.mongo.documents.SubscriptionRecord;
import com.appdirect.mongo.documents.UserRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRecordRepository extends CrudRepository<SubscriptionRecord, String> {
}
