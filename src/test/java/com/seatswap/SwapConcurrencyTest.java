package com.seatswap;

import com.seatswap.service.SwapRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.seatswap.repository.SwapRequestRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import com.seatswap.entity.*;
import com.seatswap.repository.BookingRepository;
import com.seatswap.repository.SeatRepository;
import com.seatswap.repository.PassengerRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.seatswap.dto.CreateSwapRequest;
@SpringBootTest
class SwapConcurrencyTest {

    @Autowired
    private BookingRepository bookingRepository;


    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private SwapRequestService swapRequestService;

    @Autowired
    private SwapRequestRepository swapRequestRepository;

    @Test
    void shouldAllowOnlyOneConcurrentAcceptance() throws Exception {

        int numberOfThreads = 5;

        ExecutorService executor =
                Executors.newFixedThreadPool(numberOfThreads);

        CountDownLatch startSignal =
                new CountDownLatch(1);

        List<Future<String>> results = new ArrayList<>();
        Long swapRequestId = swapRequestRepository
                .findAll()
                .stream()
                .filter(request ->
                        request.getStatus().name().equals("PENDING"))
                .findFirst()
                .orElseThrow()
                .getId();

        for (int i = 0; i < numberOfThreads; i++) {

            results.add(
                    executor.submit(() -> {

                        startSignal.await();

                        try {
                            return swapRequestService
                                    .acceptSwapRequest(swapRequestId)
                                    .getStatus();

                        } catch (Exception ex) {
                            return ex.getMessage();
                        }
                    })
            );
        }

        startSignal.countDown();

        int successfulAcceptances = 0;
        int rejectedAcceptances = 0;

        for (Future<String> result : results) {

            String response = result.get();

            System.out.println("RESULT: " + response);

            if ("COMPLETED".equals(response)) {
                successfulAcceptances++;
            } else if ("Only pending swap requests can be accepted"
                    .equals(response)) {
                rejectedAcceptances++;
            }
        }

        executor.shutdown();

        assertEquals(1, successfulAcceptances);
        assertEquals(4, rejectedAcceptances);
    }

    private Long createPendingSwapRequest() {

        Booking rahulBooking = bookingRepository
                .findByPnr("4512789632")
                .orElseThrow();

        Long requestedSeatId =
                rahulBooking.getSeat().getId().equals(1L)
                        ? 2L
                        : 1L;

        CreateSwapRequest request = new CreateSwapRequest();

        request.setPnr(rahulBooking.getPnr());
        request.setRequestedSeatId(requestedSeatId);

        return swapRequestService
                .createSwapRequest(request)
                .getId();
    }
}