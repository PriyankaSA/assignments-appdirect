package com.appdirect.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Company {

    private String uuid;
    private String country;
    private String name;
    private String phoneNumber;
    private String website;

    public Company(){

    }

    @JsonCreator
    public Company(@JsonProperty("uuid") String uuid,
                   @JsonProperty("country") String country,
                   @JsonProperty("name") String name,
                   @JsonProperty("phoneNumber") String phoneNumber,
                   @JsonProperty("website") String website) {
        this.uuid = uuid;
        this.country = country;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.website = website;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}