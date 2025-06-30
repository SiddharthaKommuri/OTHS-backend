//package com.cts.travelpackage.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer; // Required for .httpBasic(Customizer.withDefaults())
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration // Marks this class as a Spring configuration class
//@EnableWebSecurity // Enables Spring Security's web security features
//public class SecurityConfig {
//
//	@Bean // Defines a bean that Spring will manage, specifically the SecurityFilterChain
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http
//				// 1. Configure Authorization (What requests are allowed/authenticated)
//				.authorizeHttpRequests(authorize -> authorize
//						// Allow unauthenticated access to the H2 console
//						.requestMatchers("/h2-console/**").permitAll()
//						// IMPORTANT: All other requests require authentication
//						// This will prevent direct access to your API endpoints without credentials
//						.anyRequest().authenticated()
//				)
//				// 2. Configure CSRF (Cross-Site Request Forgery) protection
//				// For stateless APIs (common in microservices accessed by a Gateway), CSRF is often disabled.
//				// If your microservice serves traditional HTML forms, you should enable CSRF.
//				.csrf(csrf -> csrf
//								.disable() // Disable CSRF protection for all requests
//						// If you only wanted to ignore specific paths (like H2 console) and keep CSRF enabled for others,
//						// you would use .ignoringRequestMatchers("/h2-console/**") and NOT call .disable().
//						// However, .disable() is simpler and often suitable for backend APIs.
//				)
//				// 3. Configure Headers (Especially for H2 Console in an iframe)
//				.headers(headers -> headers
//						// Allow the H2 console to be displayed in a frame from the same origin.
//						// This is necessary because H2 console uses iframes, and Spring Security by default
//						// prevents this (X-Frame-Options: DENY).
//						.frameOptions(frameOptions -> frameOptions.sameOrigin())
//				)
//				// 4. Configure Authentication Mechanism (How users will authenticate)
//				// This enables HTTP Basic authentication. When an unauthenticated request hits an
//				// .authenticated() endpoint, a login challenge (pop-up) will appear.
//				.httpBasic(Customizer.withDefaults()); // Uses Spring Security's default HTTP Basic setup
//
//		// Build the SecurityFilterChain
//		return http.build();
//	}
//
//	// Optional: If you need to define in-memory users for testing or simple scenarios,
//	// you would add a UserDetailsService bean and a PasswordEncoder bean here.
//	// For example:
//    /*
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.builder()
//            .username("user")
//            .password(passwordEncoder().encode("password")) // Always encode passwords!
//            .roles("USER")
//            .build();
//        UserDetails admin = User.builder()
//            .username("admin")
//            .password(passwordEncoder().encode("adminpass"))
//            .roles("ADMIN", "USER")
//            .build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(); // Recommended strong password encoder
//    }
//    */
//}