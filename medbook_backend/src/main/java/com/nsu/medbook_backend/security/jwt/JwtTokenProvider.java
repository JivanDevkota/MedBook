package com.nsu.medbook_backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";
    private static final String CLAIM_TOKEN_TYPE = "token_type";

    private final SecretKey signingKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.signingKey = Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(Map.of(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS), userDetails, accessTokenExpiration);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        return generateToken(claims, userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(Map.of(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH), userDetails, refreshTokenExpiration);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails, long expirationMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            return userDetails.getUsername().equals(extractUsername(token))
                    && !isTokenExpired(token)
                    && isTokenTypeValid(token, TOKEN_TYPE_ACCESS);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        try {
            return userDetails.getUsername().equals(extractUsername(token))
                    && !isTokenExpired(token)
                    && isTokenTypeValid(token, TOKEN_TYPE_REFRESH);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isTokenTypeValid(String token, String expectedType) {
        try {
            return expectedType.equals(extractTokenType(token));
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration / 1000;
    }
}
