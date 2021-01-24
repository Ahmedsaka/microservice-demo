package io.medalytics.demo.controller;

import io.medalytics.demo.model.request.RegistrationRequest;
import io.medalytics.demo.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(registrationService.register(request));
    }

    @GetMapping(path = "/confirm-token")
    public ResponseEntity<?> confirmToken(@RequestParam("token") String confirmationToken) {
        return ResponseEntity.ok(registrationService.confirmToken(confirmationToken));
    }
}
