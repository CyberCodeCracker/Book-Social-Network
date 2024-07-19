package com.amouri.book.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService is a service class responsible for managing JSON Web Tokens (JWTs) in a Spring Security application.
 * It handles the generation, validation, and parsing of JWTs used for user authentication and authorization.
 *
 * Key Responsibilities:
 *
 * 1. **Generate JWTs:**
 *    - Generates JWTs for users, including custom claims and user details.
 *    - Signs the JWTs using a secret key to ensure integrity and authenticity.
 *
 * 2. **Validate JWTs:**
 *    - Checks if a given JWT is valid by verifying its signature and expiration.
 *    - Ensures the token is associated with the correct user and has not expired.
 *
 * 3. **Extract Claims:**
 *    - Extracts various claims from the JWT, such as the username, expiration date, and custom claims.
 *    - Provides methods to access specific claims needed for authorization decisions.
 *
 * Dependencies:
 * - Uses the `jjwt` library for JWT creation, parsing, and validation.
 * - Configures JWT expiration time and secret key through application properties.
 */

@Service
// This is the service that will generate the token, decode it, *
// extract information from it, validate it...
public class JwtService {

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    // Generates a JWT for a given user without any additional claims
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generates a JWT for a given user, including any additional claims
    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, jwtExpiration);
    }

    // Creates a JWT with custom claims, user details and an expiration time
    private String buildToken(
            Map<String, Object> extraClaims,                                                        // Map of additional claims to be included in the JWT.
            UserDetails userDetails,                                                                // Holds user specific details such as username, password and authorities
            long jwtExpiration                                                                      // The expiration time of the JWT in milliseconds
    ) {
        // Extracts the authorities ( roles or permission ) from the 'userDetails' object
        var authorities = userDetails.getAuthorities()                                              // Retrieves a collection of 'GrantedAuthority' objects associated with the user
                .stream()                                                                           // Converts the collection to a stream for processing
                .map(GrantedAuthority::getAuthority)                                                // Maps each 'GrantedAuthority' to its string representation (e.g., "ROLE_USER")
                .toList();                                                                          // Collects the mapped authorities into a list
        return Jwts
                .builder()                                                                          // Starts building a new JWT
                .setClaims(extraClaims)                                                             // Adds the custom claims from the 'extraClaims' map to the JWT payload
                .setSubject(userDetails.getUsername())                                              // Sets the subject ('sub') claim of the JWT to the username of the user
                .setIssuedAt(new Date(System.currentTimeMillis()))                                  // Sets the issued-at ('iat') claim to the current date and time
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))                // Sets the expiration ('exp') a day after issuing the token
                .claim("authorities", authorities)                                            // Adds a custom claim named "authorities" containing the list of authorities extracted earlier
                .signWith(getSignInKey())                                                           // Signs the JWT with a secret key obtained from the 'getSignInKey()' method, ensuring the token's integrity and authenticity
                .compact()                                                                          // Finalizes the JWT construction and returns it as a compact, URL-safe string
                ;
    }

    // Decodes a base64-encoded secret key and uses it to generate an HMAC-SHA key, which is used to signJWTs
    private Key getSignInKey() {
        // Converts the base64-encoded 'secretKey' string into a byte array
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // Creates an HMAC-Sha key from the decoded byte array
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // This method checks if the given JWT token is valid for the given user
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // Extract the username from the token
        final String username = extractUsername(token);
        // Validate the token by checking if the username matches and the token is not expired
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // This method checks if the given JWT token has not expired
    private boolean isTokenExpired(String token) {
        // Extract the expiration date from the token and check if it is before the current date
        return extractExpiration(token).before(new Date());
    }

    // This method extracts the expiration date from the given JWT token
    private Date extractExpiration(String token) {
        // Extract and return the expiration date from the token's claims
        return extractClaim(token, Claims::getExpiration);
    }

    // Extracts the username (subject) from the given JWT token
    public String extractUsername(String token) {
        // Use the extractClaim method to get the subject (username) from the token's claims
        return extractClaim(token, Claims::getSubject);
    }

    // Extracts a specific claim from the token using a claim resolver function
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        // Extract all claims from the token
        final Claims claims = extractAllClaims(token);
        // Use the claim resolver to extract the specific claim from the claims
        return claimResolver.apply(claims);
    }

    // Extracts all claims from the token
    private Claims extractAllClaims(String token) {
        // Parse the token and extract the claims using the secret signing key
        return Jwts                                                                     // Use the Jwts utility class from the jjwt library
                .parserBuilder()                                                        // Create a new JwtParserBuilder instance to configure and build a JwtParser
                .setSigningKey(getSignInKey())                                          // Set the signing key used to verify the token's signature. The signing key is obtained from the getSignInKey() method
                .build()                                                                // Build the JwtParser instance
                .parseClaimsJws(token)                                                  // Parse the JWT token, which is expected to be in JWS (signed JWT) format
                .getBody()                                                              // Extract and return the claims (payload) from the parsed token
                ;
    }

}
