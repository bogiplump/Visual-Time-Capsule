package com.java.web.virtual.time.capsule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController  {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}