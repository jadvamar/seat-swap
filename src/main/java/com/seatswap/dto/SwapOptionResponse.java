package com.seatswap.dto;

public class SwapOptionResponse {

    private Long bookingId;
    private Long seatId;
    private String coachNumber;
    private Integer seatNumber;
    private String berthType;
    private String passengerName;

    public SwapOptionResponse(
            Long bookingId,
            Long seatId,
            String coachNumber,
            Integer seatNumber,
            String berthType,
            String passengerName
    ) {
        this.bookingId = bookingId;
        this.seatId = seatId;
        this.coachNumber = coachNumber;
        this.seatNumber = seatNumber;
        this.berthType = berthType;
        this.passengerName = passengerName;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public String getCoachNumber() {
        return coachNumber;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public String getBerthType() {
        return berthType;
    }

    public String getPassengerName() {
        return passengerName;
    }
}