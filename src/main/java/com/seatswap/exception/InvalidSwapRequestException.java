package com.seatswap.exception;

public class InvalidSwapRequestException extends RuntimeException {

    public InvalidSwapRequestException(String message) {
        super(message);
    }
}