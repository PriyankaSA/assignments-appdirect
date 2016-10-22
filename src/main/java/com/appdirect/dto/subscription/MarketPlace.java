package com.appdirect.dto.subscription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketPlace {

    private String baseUrl;
    private String partner;

    public MarketPlace(){

    }
    @JsonCreator
    public MarketPlace(@JsonProperty("baseUrl") String baseUrl,
                       @JsonProperty("partner") String partner) {
        this.baseUrl = baseUrl;
        this.partner = partner;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }
}