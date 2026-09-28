package com.seatswap.exception;

import com.seatswap.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFound(
            BookingNotFoundException ex) {

        return new ErrorResponse(
                "BOOKING_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(
            ConstraintViolationException ex) {

        return new ErrorResponse(
                "INVALID_REQUEST",
                ex.getMessage()
        );
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(
            AuthException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "AUTH_ERROR",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(InvalidSwapRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidSwapRequest(
            InvalidSwapRequestException ex) {

        return new ErrorResponse(
                "INVALID_SWAP_REQUEST",
                ex.getMessage()
        );
    }

    @ExceptionHandler(JourneyNotActiveException.class)
    public ResponseEntity<ErrorResponse> handleJourneyNotActive(
            JourneyNotActiveException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "JOURNEY_NOT_ACTIVE",
                        ex.getMessage()
                ));
    }
}