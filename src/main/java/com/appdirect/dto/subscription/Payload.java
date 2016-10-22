package com.appdirect.dto.subscription;

import com.appdirect.dto.Account;
import com.appdirect.dto.Company;
import com.appdirect.dto.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payload {
    private Company company;
    private Order order;
    private Account account;
    private User user;

    public Payload(){

    }

    @JsonCreator
    public Payload(@JsonProperty("company") Company company,
                   @JsonProperty("order") Order order,
                   @JsonProperty("account") Account account,
                   @JsonProperty("user") User user) {
        this.company = company;
        this.order = order;
        this.account = account;
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
