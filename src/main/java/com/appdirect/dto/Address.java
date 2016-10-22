package com.appdirect.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    private String firstName;
    private String fullName;
    private String lastName;
    private String street;
    private String zip;
    private String city;
    private String country;
    private String state;

    @JsonCreator
    public Address(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("fullName") String fullName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("city") String city,
            @JsonProperty("country") String country,
            @JsonProperty("state") String state,
            @JsonProperty("street") String street,
            @JsonProperty("zip") String zip) {
        this.firstName = firstName;
        this.fullName = fullName;
        this.lastName = lastName;
        this.city = city;
        this.country = country;
        this.state = state;
        this.street = street;
        this.zip = zip;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
