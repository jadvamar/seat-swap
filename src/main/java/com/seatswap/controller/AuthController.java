package com.seatswap.controller;

import com.seatswap.dto.SignupRequest;
import com.seatswap.dto.SignupResponse;
import com.seatswap.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.seatswap.dto.LoginRequest;
import com.seatswap.dto.LoginResponse;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponse signup(
            @Valid @RequestBody SignupRequest request) {

        authService.signup(request);

        return new SignupResponse(
                "Account created successfully"
        );
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        return new LoginResponse(token);
    }
}