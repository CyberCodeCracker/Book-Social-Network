package com.amouri.book.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

// @Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    //  This filter intercepts HTTP requests to process them before they reach the intended destination
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,                        // The HTTP request object
            @NonNull HttpServletResponse response,                      // The HTTP response object
            @NonNull FilterChain filterChain                            // The filter chain to pass the request and response to the next filter or source
    ) throws ServletException, IOException {
        if(request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);                    // If true; the filter passes the request and response along the filter chain
                                                                        // without further processing
            return;                                                     // Stops further processing within this filer
        }
        final String authHeader = request.getHeader(AUTHORIZATION);     // Retrieves the value of the Authorization header from the HTTP request
        final String jwt;                                               // JWT token
        final String userEmail;                                         // Extracted user email from JWT

        // Check if the Authorization header is missing or does not start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);                    // Pass request and response along the chain
            return;                                                     // Exit the method to stop further processing
        }

        // Extract the JWT token by removing the "Bearer " prefix
        jwt = authHeader.substring(7);

        // Extract username from the JWT token
        userEmail = jwtService.extractUsername(jwt);

        // Check if the userEmail is not null and there is no existing authentication
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details using the extracted username
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Validate the JWT token with the user details
            if(jwtService.isTokenValid(jwt, userDetails)) {
                // Create an authentication token with user details and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set authentication details from the request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Pass request and response along the chain
        filterChain.doFilter(request, response);
    }
}
