package com.seatswap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateSwapRequest {

    @NotBlank(message = "PNR is required")
    @Pattern(
            regexp = "\\d{10}",
            message = "PNR must contain exactly 10 digits"
    )
    private String pnr;

    private Long bookingId;

    private Long requestedSeatId;

    public CreateSwapRequest() {
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public Long getRequestedSeatId() {
        return requestedSeatId;
    }

    public void setRequestedSeatId(Long requestedSeatId) {
        this.requestedSeatId = requestedSeatId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }




}