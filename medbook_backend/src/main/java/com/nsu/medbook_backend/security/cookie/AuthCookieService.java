package com.nsu.medbook_backend.security.cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Writes authentication JWTs as HttpOnly cookies so browser JavaScript can
 * never read the token material (mitigates XSS token theft).
 *
 * <p>The access cookie is scoped to the API context path; the refresh cookie
 * is scoped further to the auth endpoints, so it is only ever transmitted on
 * {@code /auth/refresh} and {@code /auth/logout} calls.</p>
 */
@Component
public class AuthCookieService {

    public static final String ACCESS_COOKIE_NAME = "MEDBOOK_ACCESS_TOKEN";
    public static final String REFRESH_COOKIE_NAME = "MEDBOOK_REFRESH_TOKEN";

    private final String apiContextPath;
    private final String refreshCookiePath;
    private final boolean secure;
    private final String sameSite;

    public AuthCookieService(
            @Value("${server.servlet.context-path:/}") String apiContextPath,
            @Value("${app.auth.cookie.secure:false}") boolean secure,
            @Value("${app.auth.cookie.same-site:Lax}") String sameSite
    ) {
        this.apiContextPath = apiContextPath;
        this.refreshCookiePath = apiContextPath + "/auth";
        this.secure = secure;
        this.sameSite = sameSite;
    }

    public void writeAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addHeader("Set-Cookie", buildAccessCookie(accessToken).toString());
        response.addHeader("Set-Cookie", buildRefreshCookie(refreshToken).toString());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        response.addHeader("Set-Cookie",
                baseCookie(ACCESS_COOKIE_NAME, "").maxAge(0L).path(apiContextPath).build().toString());
        response.addHeader("Set-Cookie",
                baseCookie(REFRESH_COOKIE_NAME, "").maxAge(0L).path(refreshCookiePath).build().toString());
    }

    public String extractAccessToken(HttpServletRequest request) {
        return extractCookie(request, ACCESS_COOKIE_NAME);
    }

    public String extractRefreshToken(HttpServletRequest request) {
        return extractCookie(request, REFRESH_COOKIE_NAME);
    }

    private ResponseCookie buildAccessCookie(String value) {
        return baseCookie(ACCESS_COOKIE_NAME, value)
                .path(apiContextPath)
                .build();
    }

    private ResponseCookie buildRefreshCookie(String value) {
        return baseCookie(REFRESH_COOKIE_NAME, value)
                .path(refreshCookiePath)
                .build();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName()) && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
