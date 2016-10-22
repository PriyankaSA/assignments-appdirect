package com.appdirect.web.commons;

public class ErrorResponse extends Response{
    private ErrorCode errorCode;
    private String message;

    public ErrorResponse() {
        this.success = false;
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
        this();
        this.errorCode = errorCode;
        this.message = message;
    }

    public enum ErrorCode {
        USER_ALREADY_EXISTS,
        USER_NOT_FOUND,
        ACCOUNT_NOT_FOUND,
        MAX_USERS_REACHED,
        UNAUTHORIZED,
        OPERATION_CANCELED,
        CONFIGURATION_ERROR,
        INVALID_RESPONSE,
        INVALID_REQUEST,
        PENDING,
        FORBIDDEN,
        BINDING_NOT_FOUND,
        TRANSPORT_ERROR,
        UNKNOWN_ERROR
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
