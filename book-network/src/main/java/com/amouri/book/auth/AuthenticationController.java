package com.amouri.book.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    // Maps HTTP POST requests to /register endpoint to this method.
    @PostMapping("/register")
    // Sets the HTTP status code to return when the method completes successfully to 202 Accepted.
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register (
            // Binds the method parameter to the body of the HTTP request and triggers validation
            // on the RegistrationRequest object based on constraints defined within the class.
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        // Calls the register method of the AuthenticatinoService class, passing in the request object.
        // This is where the actual registration logic is handled.
        service.register(request);
        // Constrcuts and returns a ResponseEntity with a status of 202 Accepted.
        // The build() method creates the response without a body
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate (
            @RequestBody @Valid AuthenticationResquest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
    }

}
