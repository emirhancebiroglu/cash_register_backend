package com.bit.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @GetMapping("/userManagementServiceFallBack")
    public ResponseEntity<String> userManagementGetServiceFallBack() {
        return ResponseEntity.status(503).body("User Management Service (GET) is currently unavailable. Please try again later.");
    }

    @PostMapping("/userManagementServiceFallBack")
    public ResponseEntity<String> userManagementPostServiceFallBack() {
        return ResponseEntity.status(503).body("User Management Service (POST) is currently unavailable. Please try again later.");
    }

    @DeleteMapping("/userManagementServiceFallBack")
    public ResponseEntity<String> userManagementDeleteServiceFallBack() {
        return ResponseEntity.status(503).body("User Management Service (DELETE) is currently unavailable. Please try again later.");
    }

    @GetMapping("/jwtAuthServiceFallBack")
    public ResponseEntity<String> jwtAuthServiceGetFallBack() {
        return ResponseEntity.status(503).body("Jwt Auth Service (GET) is currently unavailable. Please try again later.");
    }

    @PostMapping("/jwtAuthServiceFallBack")
    public ResponseEntity<String> jwtAuthServicePostFallBack() {
        return ResponseEntity.status(503).body("Jwt Auth Service (POST) is currently unavailable. Please try again later.");
    }

    @GetMapping("/productServiceFallBack")
    public ResponseEntity<String> productServiceGetFallBack() {
        return ResponseEntity.status(503).body("Product Service (GET) is currently unavailable. Please try again later.");
    }

    @PostMapping("/productServiceFallBack")
    public ResponseEntity<String> productServicePostFallBack() {
        return ResponseEntity.status(503).body("Product Service (POST) is currently unavailable. Please try again later.");
    }

    @DeleteMapping("/productServiceFallBack")
    public ResponseEntity<String> productServiceDeleteFallBack() {
        return ResponseEntity.status(503).body("Product Service (DELETE) is currently unavailable. Please try again later.");
    }

    @GetMapping("/salesServiceFallBack")
    public ResponseEntity<String> salesServiceGetFallBack() {
        return ResponseEntity.status(503).body("Sales Service (GET) is currently unavailable. Please try again later.");
    }

    @PostMapping("/salesServiceFallBack")
    public ResponseEntity<String> salesServicePostFallBack() {
        return ResponseEntity.status(503).body("Sales Service (POST) is currently unavailable. Please try again later.");
    }

    @DeleteMapping("/salesServiceFallBack")
    public ResponseEntity<String> salesServiceDeleteFallBack() {
        return ResponseEntity.status(503).body("Sales Service (DELETE) is currently unavailable. Please try again later.");
    }

    @GetMapping("/reportingServiceFallBack")
    public ResponseEntity<String> reportingServiceFallBack() {
        return ResponseEntity.status(503).body("Reporting Service is currently unavailable. Please try again later.");
    }
}