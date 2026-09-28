package com.seatswap.exception;

public class JourneyNotActiveException extends RuntimeException {

    public JourneyNotActiveException(String message) {
        super(message);
    }
}