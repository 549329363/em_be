package com.employee.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class PasswordTestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/password")
    public String testPassword(@RequestParam String password, @RequestParam String hash) {
        boolean matches = passwordEncoder.matches(password, hash);
        return "Password: " + password + ", Hash: " + hash + ", Matches: " + matches;
    }
    
    @GetMapping("/encode")
    public String encodePassword(@RequestParam String password) {
        String encoded = passwordEncoder.encode(password);
        return "Original: " + password + ", Encoded: " + encoded;
    }
}