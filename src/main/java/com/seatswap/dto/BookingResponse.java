package com.seatswap.dto;

public class BookingResponse {

    private Long bookingId;
    private Long passengerId;
    private String pnr;
    private String passengerName;
    private String trainNumber;
    private String trainName;
    private String coachNumber;
    private Integer seatNumber;
    private String berthType;
    private String status;

    public BookingResponse(
            Long bookingId,
            Long passengerId,
            String pnr,
            String passengerName,
            String trainNumber,
            String trainName,
            String coachNumber,
            Integer seatNumber,
            String berthType,
            String status) {

        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.coachNumber = coachNumber;
        this.seatNumber = seatNumber;
        this.berthType = berthType;
        this.status = status;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public String getPnr() {
        return pnr;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
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

    public String getStatus() {
        return status;
    }
}