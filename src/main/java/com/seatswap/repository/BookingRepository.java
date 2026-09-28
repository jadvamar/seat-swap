package com.seatswap.repository;

import com.seatswap.entity.Booking;
import com.seatswap.entity.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByPnr(String pnr);

    Optional<Booking> findByJourneyIdAndSeatIdAndStatus(
            Long journeyId,
            Long seatId,
            BookingStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.id = :bookingId
            """)
    Optional<Booking> findByIdForUpdate(Long bookingId);

    List<Booking> findByJourneyIdAndStatus(
            Long journeyId,
            BookingStatus status
    );
}