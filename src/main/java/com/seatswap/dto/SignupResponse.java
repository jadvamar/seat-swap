package com.seatswap.dto;

public class SignupResponse {

    private String message;

    public SignupResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}