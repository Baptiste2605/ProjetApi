package com.example.ProjectApp.controller;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class LoginController {

    @GetMapping("/login")

    public String login(Authentication auth) {

        if (auth != null && auth.isAuthenticated()) {

            boolean isRh = auth.getAuthorities().stream()

                    .anyMatch(a -> a.getAuthority().equals("ROLE_RH"));

            return isRh ? "redirect:/rh/employees" : "redirect:/me/payslips";

        }

        return "login";

    }

}

