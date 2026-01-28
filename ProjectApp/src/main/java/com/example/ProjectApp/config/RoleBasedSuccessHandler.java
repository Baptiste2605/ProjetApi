package com.example.ProjectApp.config;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component

public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    @Override

    public void onAuthenticationSuccess(HttpServletRequest request,

                                        HttpServletResponse response,

                                        Authentication authentication)

            throws IOException, ServletException {

        String redirectUrl = "/login"; // valeur par defaut

        for (GrantedAuthority auth : authentication.getAuthorities()) {

            String role = auth.getAuthority();

            if (role.contains("RH")) {

                redirectUrl = "/rh/employees";

                break;

            } else if (role.contains("EMP")) {

                redirectUrl = "/me/payslips";

                break;

            }

        }

        // redirige seulement APRES le login

        response.sendRedirect(redirectUrl);

    }

}

