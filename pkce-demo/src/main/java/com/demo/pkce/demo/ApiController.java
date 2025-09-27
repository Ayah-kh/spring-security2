package com.demo.pkce.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("api/home")
    public String home() {
        return "Hello, home";
    }
}
