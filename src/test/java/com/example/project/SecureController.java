package com.example.project;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secure-endpoint")
public class SecureController {

    @GetMapping
    public ResponseEntity<String> secureEndpoint() {
        return ResponseEntity.ok("Access granted");
    }
}
