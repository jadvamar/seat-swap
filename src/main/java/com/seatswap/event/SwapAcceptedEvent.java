package com.seatswap.event;

public class SwapAcceptedEvent {

    private final String requesterEmail;
    private final String requesterName;
    private final String newCoach;
    private final Integer newSeat;

    public SwapAcceptedEvent(
            String requesterEmail,
            String requesterName,
            String newCoach,
            Integer newSeat) {

        this.requesterEmail = requesterEmail;
        this.requesterName = requesterName;
        this.newCoach = newCoach;
        this.newSeat = newSeat;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getNewCoach() {
        return newCoach;
    }

    public Integer getNewSeat() {
        return newSeat;
    }
}