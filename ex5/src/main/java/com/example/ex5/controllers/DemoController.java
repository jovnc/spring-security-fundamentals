package com.example.ex5.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @GetMapping
    public String hello() {
        return "Hello, from DemoController!";
    }

    @PostMapping
    public String postHello() {
        return "Posted to DemoController!";
    }
}
