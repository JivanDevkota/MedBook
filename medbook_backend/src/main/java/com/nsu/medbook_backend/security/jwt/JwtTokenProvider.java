package com.nsu.medbook_backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.access-token-expiration:900000}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration;
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";
    private static final String CLAIM_TOKEN_TYPE = "token_type";


    /**
     * Generate access token for authenticated user
     * Default expiration: 15 minutes (900000ms)
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        return generateToken(claims, userDetails, accessTokenExpiration);
    }

    /**
     * Generate access token with additional claims
     */
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        return generateToken(extraClaims, userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH);
        return generateToken(claims, userDetails, refreshTokenExpiration);
    }

    /**
     * Validate token against user details
     * Checks username match and token expiration and token type
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username != null &&
                    username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token) &&
                    isTokenTypeValid(token, TOKEN_TYPE_ACCESS);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate refresh token specifically
     * Checks if token is marked as refresh type
     */
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username != null &&
                    username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token) &&
                    isTokenTypeValid(token, TOKEN_TYPE_REFRESH);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenTypeValid(String token, String expectedType) {
        try {
            String tokenType = extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
            return expectedType.equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    public String generateToken(
            Map<String, Object> claims,
            UserDetails userDetails,
            Long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Key getSignInKey() {
        byte[] decodeKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decodeKey);
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
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
