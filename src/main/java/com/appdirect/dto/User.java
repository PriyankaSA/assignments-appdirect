package com.appdirect.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String email;
    private String firstName;
    private String language;
    private String lastName;
    private String locale;
    private String openId;
    private String uuid;
    private Address address;

    public User(){

    }
    @JsonCreator
    public User(@JsonProperty("uuid") String uuid,
                    @JsonProperty("openId") String openId,
                    @JsonProperty("email") String email,
                    @JsonProperty("firstName") String firstName,
                    @JsonProperty("lastName") String lastName,
                    @JsonProperty("language") String language,
                    @JsonProperty("address") Address address,
                    @JsonProperty("locale") String  locale) {
        this.uuid = uuid;
        this.email = email;
        this.firstName = firstName;
        this.language = language;
        this.lastName = lastName;
        this.openId = openId;
        this.address = address;
        this.locale = locale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
