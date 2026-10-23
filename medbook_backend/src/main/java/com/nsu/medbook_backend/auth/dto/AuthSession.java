package com.nsu.medbook_backend.auth.dto;

/**
 * Internal carrier for a freshly issued session. The tokens are written to
 * HttpOnly cookies by the controller and are never serialized to the client;
 * only {@link #user()} reaches the response body.
 */
public record AuthSession(
        UserResponse user,
        String accessToken,
        String refreshToken
) {
}
