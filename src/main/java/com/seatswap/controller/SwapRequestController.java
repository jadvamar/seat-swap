package com.seatswap.controller;

import com.seatswap.dto.CreateSwapRequest;
import com.seatswap.dto.SwapOptionResponse;
import com.seatswap.dto.SwapRequestResponse;
import com.seatswap.entity.UserAccount;
import com.seatswap.security.AuthenticatedUserService;
import com.seatswap.service.SwapRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/swap-requests")
public class SwapRequestController {

    private final SwapRequestService swapRequestService;
    private final AuthenticatedUserService authenticatedUserService;

    public SwapRequestController(
            SwapRequestService swapRequestService,
            AuthenticatedUserService authenticatedUserService) {

        this.swapRequestService = swapRequestService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SwapRequestResponse createSwapRequest(
            @Valid @RequestBody CreateSwapRequest request,
            Authentication authentication) {

        UserAccount user =
                authenticatedUserService.getCurrentUser(
                        authentication
                );

        Long passengerId =
                user.getPassenger().getId();

        return swapRequestService.createSwapRequest(
                request,
                passengerId
        );
    }

    @GetMapping("/incoming")
    public List<SwapRequestResponse> getIncomingRequests(
            Authentication authentication) {

        UserAccount user =
                authenticatedUserService.getCurrentUser(
                        authentication
                );

        Long passengerId =
                user.getPassenger().getId();

        return swapRequestService.getIncomingRequests(
                passengerId
        );
    }

    @PostMapping("/{swapRequestId}/accept")
    public SwapRequestResponse acceptSwapRequest(
            @PathVariable Long swapRequestId,
            Authentication authentication) {

        UserAccount user =
                authenticatedUserService.getCurrentUser(
                        authentication
                );

        Long passengerId =
                user.getPassenger().getId();

        return swapRequestService.acceptSwapRequest(
                swapRequestId,
                passengerId
        );
    }

    @GetMapping("/options/{pnr}")
    public List<SwapOptionResponse> getSwapOptions(
            @PathVariable String pnr,
            @RequestParam Long bookingId,
            Authentication authentication) {

        UserAccount user =
                authenticatedUserService.getCurrentUser(
                        authentication
                );

        Long passengerId =
                user.getPassenger().getId();

        return swapRequestService.getSwapOptions(
                pnr,
                bookingId,
                passengerId
        );
    }

    @PostMapping("/{swapRequestId}/cancel")
    public SwapRequestResponse cancelSwapRequest(
            @PathVariable Long swapRequestId,
            @RequestParam Long bookingId,
            Authentication authentication) {

        UserAccount user =
                authenticatedUserService.getCurrentUser(
                        authentication
                );

        Long passengerId =
                user.getPassenger().getId();

        return swapRequestService.cancelSwapRequest(
                swapRequestId,
                bookingId,
                passengerId
        );
    }

    @GetMapping("/outgoing/{bookingId}")
    public List<SwapRequestResponse> getOutgoingRequests(
            @PathVariable Long bookingId,
            Authentication authentication) {

        UserAccount user =
                authenticatedUserService.getCurrentUser(
                        authentication
                );

        Long passengerId =
                user.getPassenger().getId();

        return swapRequestService.getOutgoingRequests(
                bookingId,
                passengerId
        );
    }
}
