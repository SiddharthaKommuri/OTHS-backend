package com.cts.apigateway.filters;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    public AuthenticationFilter() {
        super();
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    public static class Config {
        private String requiredRoles; // ✅ FIXED: match with properties key

        public String getRequiredRoles() {
            return requiredRoles;
        }

        public void setRequiredRoles(String requiredRoles) {
            this.requiredRoles = requiredRoles;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String authHeader = request.getHeaders().getFirst("Authorization");

            logger.info("Incoming request: {} {}", request.getMethod(), request.getURI());
            logger.debug("Authorization Header: {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);
            logger.debug("Extracted JWT Token: {}", token);

            if (!jwtUtil.validateToken(token)) {
                logger.warn("Invalid JWT token");
                return unauthorized(exchange);
            }

            String role = jwtUtil.getRoles(token); // should return comma-separated roles like "ADMIN,TRAVELER"
            String username = jwtUtil.extractUsername(token); // Extract username
            Long userId = jwtUtil.extractUserId(token); // Extract userId
            String contactNumber = jwtUtil.extractContactNumber(token); // Extract contactNumber

            logger.debug("Extracted Roles from token: {}", role);
            logger.debug("Extracted User ID from token: {}", userId);
            logger.debug("Extracted Contact Number from token: {}", contactNumber);


            if (config.getRequiredRoles() != null) {
                List<String> requiredRoles = Arrays.asList(config.getRequiredRoles().split(","));
                List<String> userRoles = Arrays.asList(role.split(","));

                boolean hasRequiredRole = requiredRoles.stream().anyMatch(userRoles::contains);

                if (!hasRequiredRole) {
                    logger.warn("User does not have required role(s): {}", config.getRequiredRoles());
                    return forbidden(exchange);
                }
            }

            logger.info("Authenticated user: {}, roles: {}, userId: {}, contactNumber: {}", username, role, userId, contactNumber);

            // Mutate the request to add custom headers for downstream services
            ServerHttpRequest.Builder mutatedRequestBuilder = request.mutate()
                    .header("X-User", username)
                    .header("X-Role", role)
                    .header("Authorization", "Bearer " + token); // Keep the Authorization header if needed downstream

            // Add new headers for userId and contactNumber
            if (userId != null) {
                mutatedRequestBuilder.header("X-User-Id", String.valueOf(userId));
            }
            if (contactNumber != null) {
                mutatedRequestBuilder.header("X-Contact-Number", contactNumber);
            }

            ServerHttpRequest mutated = mutatedRequestBuilder.build();

            return chain.filter(exchange.mutate().request(mutated).build());
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        logger.error("Responding with 401 Unauthorized");
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        logger.error("Responding with 403 Forbidden");
        return exchange.getResponse().setComplete();
    }
}