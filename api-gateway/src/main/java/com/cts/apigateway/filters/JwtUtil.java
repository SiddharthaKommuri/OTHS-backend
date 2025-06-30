package com.cts.apigateway.filters;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function; // Ensure this import is present

import javax.crypto.SecretKey; // Use javax.crypto.SecretKey for Java 17 / Spring Boot 3.2.x

import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {

    // FIX 1: Add Logger instance
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private SecretKey jwtSecret;

    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    @jakarta.annotation.PostConstruct // Use jakarta.annotation for Spring Boot 3+
    public void init() {
        this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretString));
        logger.info("JwtUtil initialized with secret key."); // Using the newly defined logger
    }


    public String extractUsername(String token) {
        // FIX 3: Call the correct private method 'extractAllClaims'
        return extractClaim(token, Claims::getSubject);
    }

    // Method to extract roles from the token, useful for downstream services or internal checks
    public List<String> extractRoles(String token) {
        // FIX 3: Call the correct private method 'extractAllClaims'
        Claims claims = extractAllClaims(token); // Corrected method call
        String rolesString = claims.get("roles", String.class);
        if (rolesString != null && !rolesString.isEmpty()) {
            return Arrays.asList(rolesString.split(","));
        }
        return List.of();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // FIX 3: Call the correct private method 'extractAllClaims'
        final Claims claims = extractAllClaims(token); // Corrected method call
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token) {
        if (jwtBlacklistService.isTokenBlacklisted(token)) {
            return false;
        }
        try {
            final String username = extractUsername(token);
            // In a real application, you might want to also validate roles if they are part of UserDetails for finer checks
            // For now, we rely on username and expiration.
            return (username!= null) && !username.isEmpty() && !isTokenExpired(token);
            
        } catch (Exception e) {
            // Log specific JWT exceptions for debugging (e.g., ExpiredJwtException, SignatureException, MalformedJwtException)
            logger.warn("JWT validation failed for token: {}", e.getMessage()); // Using the logger
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        // This part is correct for JJWT 0.11.x, using parserBuilder and parseClaimsJws
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret) // For JJWT 0.12.x+, use .verifyWith(jwtSecret)
                .build()
                .parseClaimsJws(token) // For JJWT 0.12.x+, use .parseSignedClaims(token)
                .getBody();
    }
    
    public String getRoles(String token) {
        return extractAllClaims(token).get("roles", String.class);
    }
}