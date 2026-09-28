package com.seatswap.service;

import com.seatswap.dto.CreateSwapRequest;
import com.seatswap.dto.SwapOptionResponse;
import com.seatswap.dto.SwapRequestResponse;
import com.seatswap.entity.Booking;
import com.seatswap.entity.BookingStatus;
import com.seatswap.entity.Seat;
import com.seatswap.entity.SwapRequest;
import com.seatswap.entity.SwapRequestStatus;
import com.seatswap.event.SwapAcceptedEvent;
import com.seatswap.exception.BookingNotFoundException;
import com.seatswap.exception.InvalidSwapRequestException;
import com.seatswap.exception.JourneyNotActiveException;
import com.seatswap.repository.BookingRepository;
import com.seatswap.repository.SeatRepository;
import com.seatswap.repository.SwapRequestRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SwapRequestService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SwapRequestService(
            SwapRequestRepository swapRequestRepository,
            BookingRepository bookingRepository,
            SeatRepository seatRepository,
            ApplicationEventPublisher eventPublisher) {

        this.swapRequestRepository = swapRequestRepository;
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SwapRequestResponse createSwapRequest(
            CreateSwapRequest request,
            Long passengerId) {

        List<Booking> bookings =
                bookingRepository.findByPnr(request.getPnr());

        Booking booking = bookings.stream()
                .filter(b ->
                        b.getId().equals(request.getBookingId())
                )
                .filter(b ->
                        b.getPassenger().getId()
                                .equals(passengerId)
                )
                .filter(b ->
                        b.getStatus() == BookingStatus.CONFIRMED
                )
                .findFirst()
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found"
                        )
                );

        boolean hasActiveRequest =
                swapRequestRepository
                        .existsByOfferedBookingIdAndStatus(
                                booking.getId(),
                                SwapRequestStatus.PENDING
                        );

        if (hasActiveRequest) {
            throw new InvalidSwapRequestException(
                    "You already have an active swap request"
            );
        }

        Seat requestedSeat =
                seatRepository.findById(
                        request.getRequestedSeatId()
                ).orElseThrow(() ->
                        new InvalidSwapRequestException(
                                "Requested seat not found"
                        )
                );

        if (!requestedSeat.getCoach()
                .getJourney()
                .getId()
                .equals(booking.getJourney().getId())) {

            throw new InvalidSwapRequestException(
                    "Requested seat does not belong to the same journey"
            );
        }

        boolean seatAlreadyRequested =
                swapRequestRepository
                        .existsByRequestedSeatIdAndStatus(
                                requestedSeat.getId(),
                                SwapRequestStatus.PENDING
                        );

        if (seatAlreadyRequested) {
            throw new InvalidSwapRequestException(
                    "This seat already has a pending swap request"
            );
        }

        Booking requestedSeatBooking =
                bookingRepository
                        .findByJourneyIdAndSeatIdAndStatus(
                                booking.getJourney().getId(),
                                requestedSeat.getId(),
                                BookingStatus.CONFIRMED
                        )
                        .orElseThrow(() ->
                                new InvalidSwapRequestException(
                                        "Requested seat is not occupied by a confirmed passenger"
                                )
                        );

        if (booking.getPassenger().getId()
                .equals(requestedSeatBooking.getPassenger().getId())) {

            throw new InvalidSwapRequestException(
                    "You cannot request a seat belonging to your own booking"
            );
        }

        // Validate that both bookings belong to the same coach type.
        String offeredCoachType =
                booking.getSeat()
                        .getCoach()
                        .getCoachType();

        String requestedCoachType =
                requestedSeatBooking.getSeat()
                        .getCoach()
                        .getCoachType();

        if (!offeredCoachType.equals(requestedCoachType)) {
            throw new InvalidSwapRequestException(
                    "Seat swap is allowed only within the same coach type"
            );
        }

        if (booking.getSeat()
                .getId()
                .equals(requestedSeat.getId())) {

            throw new InvalidSwapRequestException(
                    "You cannot request the seat you already occupy"
            );
        }

        if (!LocalDateTime.now().isBefore(
                booking.getJourney()
                        .getDepartureDateTime())) {

            throw new JourneyNotActiveException(
                    "Seat swaps are no longer allowed because the train has departed."
            );
        }

        SwapRequest swapRequest =
                new SwapRequest();

        swapRequest.setRequester(
                booking.getPassenger()
        );

        swapRequest.setOfferedBooking(
                booking
        );

        swapRequest.setOfferedSeat(
                booking.getSeat()
        );

        swapRequest.setRequestedSeat(
                requestedSeat
        );

        swapRequest.setRequestedBooking(
                requestedSeatBooking
        );

        swapRequest.setStatus(
                SwapRequestStatus.PENDING
        );

        SwapRequest savedRequest;

        try {
            savedRequest =
                    swapRequestRepository.save(
                            swapRequest
                    );
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidSwapRequestException(
                    "This seat already has a pending swap request"
            );
        }

        return new SwapRequestResponse(
                savedRequest.getId(),
                savedRequest.getRequester().getName(),
                savedRequest.getOfferedBooking().getPnr(),
                savedRequest.getOfferedBooking()
                        .getSeat()
                        .getCoach()
                        .getCoachNumber(),
                savedRequest.getOfferedBooking()
                        .getSeat()
                        .getSeatNumber(),
                savedRequest.getRequestedSeat()
                        .getCoach()
                        .getCoachNumber(),
                savedRequest.getRequestedSeat()
                        .getSeatNumber(),
                savedRequest.getStatus().name()
        );
    }

    public List<SwapRequestResponse> getIncomingRequests(
            Long passengerId) {

        List<SwapRequest> requests =
                swapRequestRepository
                        .findByRequestedBookingPassengerIdAndStatus(
                                passengerId,
                                SwapRequestStatus.PENDING
                        );

        List<SwapRequestResponse> responses =
                new ArrayList<>();

        for (SwapRequest swapRequest : requests) {

            responses.add(
                    new SwapRequestResponse(
                            swapRequest.getId(),
                            swapRequest.getRequester().getName(),
                            swapRequest.getOfferedBooking().getPnr(),
                            swapRequest.getOfferedBooking()
                                    .getSeat()
                                    .getCoach()
                                    .getCoachNumber(),
                            swapRequest.getOfferedBooking()
                                    .getSeat()
                                    .getSeatNumber(),
                            swapRequest.getRequestedSeat()
                                    .getCoach()
                                    .getCoachNumber(),
                            swapRequest.getRequestedSeat()
                                    .getSeatNumber(),
                            swapRequest.getStatus().name()
                    )
            );
        }

        return responses;
    }

    @Transactional
    public SwapRequestResponse acceptSwapRequest(
            Long swapRequestId,
            Long passengerId) {

        SwapRequest swapRequest =
                swapRequestRepository.findByIdForUpdate(
                        swapRequestId
                ).orElseThrow(() ->
                        new InvalidSwapRequestException(
                                "Swap request not found"
                        )
                );

        if (swapRequest.getStatus()
                != SwapRequestStatus.PENDING) {

            throw new InvalidSwapRequestException(
                    "Only pending swap requests can be accepted"
            );
        }

        Booking offeredBooking =
                swapRequest.getOfferedBooking();

        Booking requestedBooking =
                swapRequest.getRequestedBooking();

        /*
         * AUTHORIZATION
         *
         * The passenger whose seat was requested
         * is the only passenger allowed to accept.
         */
        if (!requestedBooking.getPassenger()
                .getId()
                .equals(passengerId)) {

            throw new InvalidSwapRequestException(
                    "You are not allowed to accept this swap request"
            );
        }

        if (!LocalDateTime.now().isBefore(
                offeredBooking.getJourney()
                        .getDepartureDateTime())) {

            throw new JourneyNotActiveException(
                    "Seat swaps are no longer allowed because the train has departed."
            );
        }

        if (!requestedBooking.getSeat()
                .getId()
                .equals(
                        swapRequest.getRequestedSeat().getId()
                )) {

            throw new InvalidSwapRequestException(
                    "Swap request is no longer valid because the requested passenger changed seats"
            );
        }

        if (!offeredBooking.getSeat()
                .getId()
                .equals(
                        swapRequest.getOfferedSeat().getId()
                )) {

            throw new InvalidSwapRequestException(
                    "Swap request is no longer valid because the requesting passenger changed seats"
            );
        }

        Long firstBookingId =
                Math.min(
                        offeredBooking.getId(),
                        requestedBooking.getId()
                );

        Long secondBookingId =
                Math.max(
                        offeredBooking.getId(),
                        requestedBooking.getId()
                );

        Booking firstBooking =
                bookingRepository.findByIdForUpdate(
                        firstBookingId
                ).orElseThrow(() ->
                        new InvalidSwapRequestException(
                                "Booking not found"
                        )
                );

        Booking secondBooking =
                bookingRepository.findByIdForUpdate(
                        secondBookingId
                ).orElseThrow(() ->
                        new InvalidSwapRequestException(
                                "Booking not found"
                        )
                );

        Booking lockedOfferedBooking =
                firstBooking.getId()
                        .equals(offeredBooking.getId())
                        ? firstBooking
                        : secondBooking;

        Booking lockedRequestedBooking =
                firstBooking.getId()
                        .equals(requestedBooking.getId())
                        ? firstBooking
                        : secondBooking;

        /*
         * Re-check authorization and state after
         * acquiring the booking locks.
         */
        if (!lockedRequestedBooking.getPassenger()
                .getId()
                .equals(passengerId)) {

            throw new InvalidSwapRequestException(
                    "You are not allowed to accept this swap request"
            );
        }

        if (lockedOfferedBooking.getStatus()
                != BookingStatus.CONFIRMED
                ||
                lockedRequestedBooking.getStatus()
                        != BookingStatus.CONFIRMED) {

            throw new InvalidSwapRequestException(
                    "Both bookings must still be confirmed"
            );
        }

        Seat offeredSeat =
                lockedOfferedBooking.getSeat();

        Seat requestedSeat =
                lockedRequestedBooking.getSeat();

        /*
         * Exchange the seats.
         */
        lockedOfferedBooking.setSeat(
                requestedSeat
        );

        lockedRequestedBooking.setSeat(
                offeredSeat
        );

        swapRequest.setStatus(
                SwapRequestStatus.COMPLETED
        );

        swapRequestRepository.save(
                swapRequest
        );

        /*
         * The requester receives the requested passenger's
         * original seat.
         *
         * Example:
         * Rahul -> B2/31
         * Priya -> B2/33
         *
         * After swap:
         * Rahul -> B2/33
         * Priya -> B2/31
         *
         * Therefore Rahul's new seat is requestedSeat.
         */
        eventPublisher.publishEvent(
                new SwapAcceptedEvent(
                        swapRequest.getRequester().getEmail(),
                        swapRequest.getRequester().getName(),
                        requestedSeat.getCoach().getCoachNumber(),
                        requestedSeat.getSeatNumber()
                )
        );

        return new SwapRequestResponse(
                swapRequest.getId(),
                swapRequest.getRequester().getName(),
                swapRequest.getOfferedBooking().getPnr(),
                lockedOfferedBooking.getSeat()
                        .getCoach()
                        .getCoachNumber(),
                lockedOfferedBooking.getSeat()
                        .getSeatNumber(),
                lockedRequestedBooking.getSeat()
                        .getCoach()
                        .getCoachNumber(),
                lockedRequestedBooking.getSeat()
                        .getSeatNumber(),
                swapRequest.getStatus().name()
        );
    }

    public List<SwapOptionResponse> getSwapOptions(
            String pnr,
            Long bookingId,
            Long passengerId) {

        List<Booking> bookings =
                bookingRepository.findByPnr(pnr);

        Booking booking =
                bookings.stream()
                        .filter(b ->
                                b.getId()
                                        .equals(bookingId)
                        )
                        .filter(b ->
                                b.getPassenger()
                                        .getId()
                                        .equals(passengerId)
                        )
                        .filter(b ->
                                b.getStatus()
                                        == BookingStatus.CONFIRMED
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new BookingNotFoundException(
                                        "Booking not found"
                                )
                        );

        List<Booking> confirmedBookings =
                bookingRepository
                        .findByJourneyIdAndStatus(
                                booking.getJourney().getId(),
                                BookingStatus.CONFIRMED
                        );

        String requesterCoachType =
                booking.getSeat()
                        .getCoach()
                        .getCoachType();

        return confirmedBookings.stream()
                // Do not show the requester's own booking.
                .filter(otherBooking ->
                        !otherBooking.getId()
                                .equals(booking.getId())
                )

                // Do not show another booking belonging
                // to the same passenger.
                .filter(otherBooking ->
                        !otherBooking.getPassenger()
                                .getId()
                                .equals(passengerId)
                )

                // Only show seats from the same coach type.
                .filter(otherBooking ->
                        otherBooking.getSeat()
                                .getCoach()
                                .getCoachType()
                                .equals(requesterCoachType)
                )

                .map(otherBooking ->
                        new SwapOptionResponse(
                                otherBooking.getId(),
                                otherBooking.getSeat().getId(),
                                otherBooking.getSeat()
                                        .getCoach()
                                        .getCoachNumber(),
                                otherBooking.getSeat()
                                        .getSeatNumber(),
                                otherBooking.getSeat()
                                        .getBerthType()
                                        .name(),
                                otherBooking.getPassenger()
                                        .getName()
                        )
                )
                .toList();
    }

    @Transactional
    public SwapRequestResponse cancelSwapRequest(
            Long swapRequestId,
            Long bookingId,
            Long passengerId) {

        SwapRequest swapRequest =
                swapRequestRepository.findByIdForUpdate(
                        swapRequestId
                ).orElseThrow(() ->
                        new InvalidSwapRequestException(
                                "Swap request not found"
                        )
                );

        Booking offeredBooking =
                swapRequest.getOfferedBooking();

        /*
         * The booking supplied by the client must
         * actually be the booking that created the request.
         */
        if (!offeredBooking.getId()
                .equals(bookingId)) {

            throw new InvalidSwapRequestException(
                    "You are not allowed to cancel this swap request"
            );
        }

        /*
         * The authenticated passenger must own
         * the offered booking.
         */
        if (!offeredBooking.getPassenger()
                .getId()
                .equals(passengerId)) {

            throw new InvalidSwapRequestException(
                    "You are not allowed to cancel this swap request"
            );
        }

        if (swapRequest.getStatus()
                != SwapRequestStatus.PENDING) {

            throw new InvalidSwapRequestException(
                    "Only pending swap requests can be cancelled"
            );
        }

        swapRequest.setStatus(
                SwapRequestStatus.CANCELLED
        );

        SwapRequest savedRequest =
                swapRequestRepository.save(
                        swapRequest
                );

        return new SwapRequestResponse(
                savedRequest.getId(),
                savedRequest.getRequester().getName(),
                savedRequest.getOfferedBooking().getPnr(),
                savedRequest.getOfferedBooking()
                        .getSeat()
                        .getCoach()
                        .getCoachNumber(),
                savedRequest.getOfferedBooking()
                        .getSeat()
                        .getSeatNumber(),
                savedRequest.getRequestedSeat()
                        .getCoach()
                        .getCoachNumber(),
                savedRequest.getRequestedSeat()
                        .getSeatNumber(),
                savedRequest.getStatus().name()
        );
    }

    public List<SwapRequestResponse> getOutgoingRequests(
            Long bookingId,
            Long passengerId) {

        Booking booking =
                bookingRepository.findById(
                        bookingId
                ).orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found"
                        )
                );

        /*
         * Prevent a user from viewing another
         * passenger's outgoing requests.
         */
        if (!booking.getPassenger()
                .getId()
                .equals(passengerId)) {

            throw new BookingNotFoundException(
                    "Booking not found"
            );
        }

        List<SwapRequest> requests =
                swapRequestRepository
                        .findByOfferedBookingIdAndStatus(
                                bookingId,
                                SwapRequestStatus.PENDING
                        );

        return requests.stream()
                .map(swapRequest ->
                        new SwapRequestResponse(
                                swapRequest.getId(),
                                swapRequest.getRequester().getName(),
                                swapRequest.getOfferedBooking().getPnr(),
                                swapRequest.getOfferedBooking()
                                        .getSeat()
                                        .getCoach()
                                        .getCoachNumber(),
                                swapRequest.getOfferedBooking()
                                        .getSeat()
                                        .getSeatNumber(),
                                swapRequest.getRequestedSeat()
                                        .getCoach()
                                        .getCoachNumber(),
                                swapRequest.getRequestedSeat()
                                        .getSeatNumber(),
                                swapRequest.getStatus().name()
                        )
                )
                .toList();
    }
}