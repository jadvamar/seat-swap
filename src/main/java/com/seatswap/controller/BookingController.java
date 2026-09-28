package com.seatswap.controller;

import com.seatswap.dto.BookingResponse;
import com.seatswap.entity.UserAccount;
import com.seatswap.security.AuthenticatedUserService;
import com.seatswap.service.BookingService;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;
    private final AuthenticatedUserService authenticatedUserService;

    public BookingController(
            BookingService bookingService,
            AuthenticatedUserService authenticatedUserService) {

        this.bookingService = bookingService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping("/{pnr}")
    public List<BookingResponse> getBookingsByPnr(
            @PathVariable
            @Pattern(
                    regexp = "\\d{10}",
                    message = "PNR must contain exactly 10 digits"
            )
            String pnr,
            Authentication authentication) {

        UserAccount user =
                authenticatedUserService.getCurrentUser(
                        authentication
                );

        Long passengerId =
                user.getPassenger().getId();

        return bookingService.getBookingsByPnr(
                pnr,
                passengerId
        );
    }
}

