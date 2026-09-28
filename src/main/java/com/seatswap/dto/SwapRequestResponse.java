package com.seatswap.dto;

public class SwapRequestResponse {

    private Long id;
    private String requesterName;
    private String offeredPnr;
    private String currentCoach;
    private Integer currentSeat;
    private String requestedCoach;
    private Integer requestedSeat;
    private String status;

    public SwapRequestResponse(
            Long id,
            String requesterName,
            String offeredPnr,
            String currentCoach,
            Integer currentSeat,
            String requestedCoach,
            Integer requestedSeat,
            String status) {

        this.id = id;
        this.requesterName = requesterName;
        this.offeredPnr = offeredPnr;
        this.currentCoach = currentCoach;
        this.currentSeat = currentSeat;
        this.requestedCoach = requestedCoach;
        this.requestedSeat = requestedSeat;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getOfferedPnr() {
        return offeredPnr;
    }

    public String getCurrentCoach() {
        return currentCoach;
    }

    public Integer getCurrentSeat() {
        return currentSeat;
    }

    public String getRequestedCoach() {
        return requestedCoach;
    }

    public Integer getRequestedSeat() {
        return requestedSeat;
    }

    public String getStatus() {
        return status;
    }
}