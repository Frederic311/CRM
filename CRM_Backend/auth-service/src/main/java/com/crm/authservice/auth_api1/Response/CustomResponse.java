package com.crm.authservice.auth_api1.Response;

public class CustomResponse<T> {
    private T data;
    private String error;

    // Constructor for success response
    public CustomResponse(T data) {
        this.data = data;
    }

    // Constructor for error response
    public CustomResponse(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
