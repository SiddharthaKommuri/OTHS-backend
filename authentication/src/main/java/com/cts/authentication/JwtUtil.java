package com.cts.authentication;

import java.util.Date;
import java.util.HashMap;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
// Use javax.crypto.SecretKey for Java 17 / Spring Boot 3.2.x
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.function.Function;

import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

import com.cts.authentication.service.JwtBlacklistService;
import com.cts.authentication.entity.User; // Import the User entity [cite: 72]

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

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // ADD THIS LINE TO INCLUDE ROLES IN THE JWT CLAIMS
        // Assuming userDetails.getAuthorities() provides the roles, e.g., [ROLE_ADMIN, ROLE_TRAVELER]
        // Convert them to a comma-separated string if that's how your downstream services expect it.
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles); // Add roles as a claim named "roles"

        // Add additional user details to the claims
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            User user = customUserDetails.getUser(); // Get the User entity from CustomUserDetails [cite: 82]
            if (user != null) {
                claims.put("userId", user.getUserId()); // Add userId [cite: 3]
                claims.put("username", user.getName()); // Add username (name field in User entity) [cite: 4]
                claims.put("contactNumber", user.getContactNumber()); // Add contactNumber [cite: 4]
            }
        }

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }

    public String extractUsername(String token) {
        // FIX 3: Call the correct private method 'extractAllClaims'
        return extractClaim(token, Claims::getSubject);
    }

    // Method to extract roles from the token, useful for downstream services or internal checks
    public List<String> extractRoles(String token) {
        // FIX 3: Call the correct private method 'extractAllClaims'
        Claims claims = extractAllClaims(token);
        // Corrected method call
        String rolesString = claims.get("roles", String.class);
        if (rolesString != null && !rolesString.isEmpty()) {
            return Arrays.asList(rolesString.split(","));
        }
        return List.of();
    }

    // New methods to extract additional claims
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public String extractName(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    public String extractContactNumber(String token) {
        return extractClaim(token, claims -> claims.get("contactNumber", String.class));
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // FIX 3: Call the correct private method 'extractAllClaims'
        final Claims claims = extractAllClaims(token);
        // Corrected method call
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        if (jwtBlacklistService.isTokenBlacklisted(token)) {
            return false;
        }
        try {
            final String username = extractUsername(token);
            // In a real application, you might want to also validate roles if they are part of UserDetails for finer checks
            // For now, we rely on username and expiration.
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            // Log specific JWT exceptions for debugging (e.g., ExpiredJwtException, SignatureException, MalformedJwtException)
            logger.warn("JWT validation failed for token: {}", e.getMessage());
            // Using the logger
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
}