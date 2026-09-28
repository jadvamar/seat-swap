package com.seatswap.security;

import com.seatswap.entity.UserAccount;
import com.seatswap.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private final UserAccountRepository userAccountRepository;

    public AuthenticatedUserService(
            UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userAccountRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        ));
    }
}