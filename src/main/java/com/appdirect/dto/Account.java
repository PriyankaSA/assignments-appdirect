package com.appdirect.dto;

import com.appdirect.dto.subscription.Item;
import com.appdirect.dto.subscription.PricingDuration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    private String accountId;
    private Status status;

    public Account(){

    }
    @JsonCreator
    public Account(@JsonProperty("accountIdentifier") String accountIdentifier,
                   @JsonProperty("status") Status status){
        this.accountId = accountIdentifier;
        this.status = status;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status{
        ACTIVE, INACTIVE
    }
}