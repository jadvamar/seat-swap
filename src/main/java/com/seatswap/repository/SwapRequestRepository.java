package com.seatswap.repository;

import com.seatswap.entity.SwapRequest;
import com.seatswap.entity.SwapRequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {

    List<SwapRequest> findByRequestedSeatIdAndOfferedBookingJourneyIdAndStatus(
            Long requestedSeatId,
            Long journeyId,
            SwapRequestStatus status
    );
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s
        FROM SwapRequest s
        WHERE s.id = :swapRequestId
        """)
    Optional<SwapRequest> findByIdForUpdate(Long swapRequestId);

    boolean existsByOfferedBookingIdAndStatus(
            Long offeredBookingId,
            SwapRequestStatus status
    );

    boolean existsByRequestedSeatIdAndStatus(
            Long requestedSeatId,
            SwapRequestStatus status
    );

    List<SwapRequest> findByOfferedBookingIdAndStatus(
            Long offeredBookingId,
            SwapRequestStatus status
    );

    List<SwapRequest> findByRequestedBookingPassengerIdAndStatus(
            Long passengerId,
            SwapRequestStatus status
    );


}