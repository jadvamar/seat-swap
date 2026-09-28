package com.seatswap.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "swap_requests")
public class SwapRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private Passenger requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offered_booking_id", nullable = false)
    private Booking offeredBooking;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_seat_id", nullable = false)
    private Seat requestedSeat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_booking_id", nullable = false)
    private Booking requestedBooking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offered_seat_id", nullable = false)
    private Seat offeredSeat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SwapRequestStatus status;

    public SwapRequest() {
    }

    public Long getId() {
        return id;
    }

    public Passenger getRequester() {
        return requester;
    }

    public void setRequester(Passenger requester) {
        this.requester = requester;
    }

    public Booking getOfferedBooking() {
        return offeredBooking;
    }

    public void setOfferedBooking(Booking offeredBooking) {
        this.offeredBooking = offeredBooking;
    }

    public Seat getRequestedSeat() {
        return requestedSeat;
    }

    public void setRequestedSeat(Seat requestedSeat) {
        this.requestedSeat = requestedSeat;
    }

    public Seat getOfferedSeat() {
        return offeredSeat;
    }

    public void setOfferedSeat(Seat offeredSeat) {
        this.offeredSeat = offeredSeat;
    }

    public Booking getRequestedBooking() {
        return requestedBooking;
    }

    public void setRequestedBooking(Booking requestedBooking) {
        this.requestedBooking = requestedBooking;
    }

    public SwapRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SwapRequestStatus status) {
        this.status = status;
    }
}