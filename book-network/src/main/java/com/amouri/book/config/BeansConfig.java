package com.amouri.book.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpHeaders.*;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    /* private final UserDetailsService userDetailsService;
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuditorAware<Integer> auditorAware() {
        return new ApplicationAuditAware();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } */

    // Marks this method as a Spring bean that will be managed by the Spring container.
    @Bean
    public CorsFilter corsFilter() {
        // Create a source for CORS configuration based on URL patterns.
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Instantiate a new CORS configuration object.
        final CorsConfiguration config = new CorsConfiguration();

        // Allow credentials such as cookies to be included in CORS requests.
        config.setAllowCredentials(true);

        // Specify the allowed origin for CORS requests (e.g., the frontend application running on localhost:4200).
        config.setAllowedOrigins(List.of("http://localhost:4200", "https://localhost:8080"));

        // Define the headers that are allowed to be included in CORS requests.
        config.setAllowedHeaders(Arrays.asList(
                ORIGIN,          // Allows the 'Origin' header.
                CONTENT_TYPE,    // Allows the 'Content-Type' header.
                ACCEPT,          // Allows the 'Accept' header.
                AUTHORIZATION    // Allows the 'Authorization' header.
        ));

        // Define the HTTP methods that are allowed for CORS requests.
        config.setAllowedMethods(Arrays.asList(
                "GET",    // Allows the 'GET' method.
                "POST",   // Allows the 'POST' method.
                "DELETE", // Allows the 'DELETE' method.
                "PUT",    // Allows the 'PUT' method.
                "PATCH"   // Allows the 'PATCH' method.
        ));

        // Register the CORS configuration for all URL patterns (/**).
        source.registerCorsConfiguration("/**", config);

        // Create and return a new CorsFilter with the defined configuration source.
        return new CorsFilter(source);
    }


}
