package com.appdirect.web.commons;

public class SuccessResponse extends Response{
    private String accountIdentifier;

    public SuccessResponse() {
        this.success = true;
    }

    public SuccessResponse(String accountIdentifier) {
        this();
        this.accountIdentifier = accountIdentifier;
    }

    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }
}
