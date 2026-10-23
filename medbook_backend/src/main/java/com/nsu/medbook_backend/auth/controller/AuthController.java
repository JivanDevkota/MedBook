package com.nsu.medbook_backend.auth.controller;

import com.nsu.medbook_backend.auth.dto.AuthSession;
import com.nsu.medbook_backend.auth.dto.LoginRequest;
import com.nsu.medbook_backend.auth.dto.RegisterRequest;
import com.nsu.medbook_backend.auth.dto.UserResponse;
import com.nsu.medbook_backend.auth.service.AuthService;
import com.nsu.medbook_backend.security.MyUser;
import com.nsu.medbook_backend.security.cookie.AuthCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;

    /**
     * Exposes the CSRF token so the SPA can bootstrap the double-submit
     * pattern: the token is written to the XSRF-TOKEN cookie and echoed back
     * in the X-XSRF-TOKEN header on every mutating request.
     */
    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request, HttpServletResponse response
    ) {
        AuthSession session = authService.register(request);
        authCookieService.writeAuthCookies(response, session.accessToken(), session.refreshToken());
        return session.user();
    }

    @PostMapping("/login")
    public UserResponse login(
            @Valid @RequestBody LoginRequest request, HttpServletResponse response
    ) {
        AuthSession session = authService.login(request);
        authCookieService.writeAuthCookies(response, session.accessToken(), session.refreshToken());
        return session.user();
    }

    /**
     * Rotates the session: a valid refresh cookie yields a fresh access and
     * refresh cookie pair. The refresh cookie is path-scoped to /auth, so it
     * only travels on this endpoint (and logout).
     */
    @PostMapping("/refresh")
    public UserResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        AuthSession session = authService.refresh(authCookieService.extractRefreshToken(request));
        authCookieService.writeAuthCookies(response, session.accessToken(), session.refreshToken());
        return session.user();
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal MyUser principal) {
        return UserResponse.from(principal.getUser());
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        authCookieService.clearAuthCookies(response);
    }
}
