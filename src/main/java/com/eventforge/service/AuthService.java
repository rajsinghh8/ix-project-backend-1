package com.eventforge.service;

import com.eventforge.api.AuthDtos;
import com.eventforge.domain.User;
import com.eventforge.exception.AuthenticationFailedException;
import com.eventforge.exception.DomainConflictException;
import com.eventforge.repository.UserRepository;
import com.eventforge.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DomainConflictException("An account with this email already exists");
        }
        User user = userRepository.save(new User(request.name().trim(), email, passwordEncoder.encode(request.password())));
        return authenticatedResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(AuthenticationFailedException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationFailedException();
        }
        return authenticatedResponse(user);
    }

    private AuthDtos.AuthResponse authenticatedResponse(User user) {
        return new AuthDtos.AuthResponse(
                jwtService.generateToken(user.getId()),
                "Bearer",
                new AuthDtos.UserResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt())
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
