package com.seatswap.service;

import com.seatswap.dto.LoginRequest;
import com.seatswap.dto.SignupRequest;
import com.seatswap.entity.Passenger;
import com.seatswap.entity.UserAccount;
import com.seatswap.exception.AuthException;
import com.seatswap.repository.PassengerRepository;
import com.seatswap.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            PassengerRepository passengerRepository,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.passengerRepository = passengerRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(LoginRequest request) {

        UserAccount userAccount =
                userAccountRepository.findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new AuthException(
                                        "Invalid email or password"
                                ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                userAccount.getPasswordHash())) {

            throw new AuthException(
                    "Invalid email or password"
            );
        }

        return jwtService.generateToken(
                userAccount.getEmail()
        );
    }

    @Transactional
    public void signup(SignupRequest request) {

        if (userAccountRepository
                .findByEmail(request.getEmail())
                .isPresent()) {

            throw new AuthException(
                    "An account with this email already exists"
            );
        }

        Passenger passenger =
                passengerRepository
                        .findByEmail(request.getEmail())
                        .orElseGet(() -> {

                            Passenger newPassenger =
                                    new Passenger();

                            newPassenger.setName(
                                    request.getName()
                            );

                            newPassenger.setEmail(
                                    request.getEmail()
                            );

                            return passengerRepository.save(
                                    newPassenger
                            );
                        });

        UserAccount userAccount =
                new UserAccount();

        userAccount.setEmail(
                request.getEmail()
        );

        String hashedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        userAccount.setPasswordHash(
                hashedPassword
        );

        userAccount.setPassenger(
                passenger
        );

        userAccountRepository.save(
                userAccount
        );
    }
}