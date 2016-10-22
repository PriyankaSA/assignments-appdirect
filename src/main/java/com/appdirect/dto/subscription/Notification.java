package com.appdirect.dto.subscription;

import com.appdirect.dto.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {
    private Type type;
    private MarketPlace marketplace;
    private User creator;
    private Payload payload;

    public Notification(){

    }

    @JsonCreator
    public Notification(@JsonProperty("type") Type type,
                        @JsonProperty("marketplace") MarketPlace marketplace,
                        @JsonProperty("creator") User creator,
                        @JsonProperty("payload") Payload payload) {
        this.type = type;
        this.marketplace = marketplace;
        this.creator = creator;
        this.payload = payload;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public MarketPlace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(MarketPlace marketplace) {
        this.marketplace = marketplace;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public enum Type {
        SUBSCRIPTION_ORDER,
        SUBSCRIPTION_CHANGE,
        SUBSCRIPTION_CANCEL,
        USER_ASSIGNMENT,
        USER_UNASSIGNMENT
    }
}
