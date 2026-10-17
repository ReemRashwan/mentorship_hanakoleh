package com.mentorship.hanakoleh.domain.user.controller;

import com.mentorship.hanakoleh.domain.user.dto.AuthResponse;
import com.mentorship.hanakoleh.domain.user.dto.LoginRequest;
import com.mentorship.hanakoleh.domain.user.dto.ResetPasswordRequest;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import com.mentorship.hanakoleh.security.JwtTokenProvider;
import com.mentorship.hanakoleh.security.TokenRevocationService;
import com.mentorship.hanakoleh.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and token generation")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerService customerService;
    private final TokenRevocationService tokenRevocationService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and generate JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userPrincipal.getUsername(), userPrincipal.getId());

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build());
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset password with token (unauthenticated)")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        customerService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                     WebRequest webRequest) {
        revokeTokenIfPresent(webRequest);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
    }

    private void revokeTokenIfPresent(WebRequest webRequest) {
        String authorizationHeader = webRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            tokenRevocationService.revoke(authorizationHeader.substring(7));
        }
    }
}
