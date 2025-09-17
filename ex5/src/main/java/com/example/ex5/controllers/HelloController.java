package com.example.ex5.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping
    public String sayHello() {
        return "Hello, from HelloController!";
    }

    @PostMapping
    public String postHello() {
        return "Posted to HelloController!";
    }
}
