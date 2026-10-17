package com.mentorship.hanakoleh.domain.user.controller;

import com.mentorship.hanakoleh.domain.user.dto.AuthResponse;
import com.mentorship.hanakoleh.domain.user.dto.LoginRequest;
import com.mentorship.hanakoleh.domain.user.dto.ResetPasswordRequest;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import com.mentorship.hanakoleh.security.JwtTokenProvider;
import com.mentorship.hanakoleh.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and token generation")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerService customerService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and generate JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Integer customerId = userPrincipal.getId();

        String token = jwtTokenProvider.generateToken(userPrincipal.getUsername(), customerId);

        AuthResponse response = AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset password with token (unauthenticated)")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        customerService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
