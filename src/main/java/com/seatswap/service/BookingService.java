
        package com.seatswap.service;

import com.seatswap.dto.BookingResponse;
import com.seatswap.entity.Booking;
import com.seatswap.entity.BookingStatus;
import com.seatswap.exception.BookingNotFoundException;
import com.seatswap.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<BookingResponse> getBookingsByPnr(
            String pnr,
            Long passengerId) {

        List<Booking> bookings =
                bookingRepository.findByPnr(pnr);

        List<Booking> passengerBookings =
                bookings.stream()
                        .filter(booking ->
                                booking.getPassenger().getId()
                                        .equals(passengerId)
                        )
                        .filter(booking ->
                                booking.getStatus()
                                        == BookingStatus.CONFIRMED
                        )
                        .toList();

        if (passengerBookings.isEmpty()) {
            throw new BookingNotFoundException(
                    "Booking not found"
            );
        }

        return passengerBookings.stream()
                .map(booking ->
                        new BookingResponse(
                                booking.getId(),
                                booking.getPassenger().getId(),
                                booking.getPnr(),
                                booking.getPassenger().getName(),
                                booking.getJourney().getTrain().getTrainNumber(),
                                booking.getJourney().getTrain().getTrainName(),
                                booking.getSeat().getCoach().getCoachNumber(),
                                booking.getSeat().getSeatNumber(),
                                booking.getSeat().getBerthType().name(),
                                booking.getStatus().name()
                        )
                )
                .toList();
    }
}

