package com.ayush.TCETian.Controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/home")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public String home() {
        return "Welcome to the Home Page!";
    }

}

