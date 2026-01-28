package com.example.ProjectApp.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class HomeController {

    /**

     * Redirige toujours vers /login lorsqu'on arrive sur /

     * Cela remplace le comportement par défaut de index.html

     */

    @GetMapping("/")

    public String redirectToLogin() {

        return "redirect:/login";

    }

    @GetMapping("/easter-egg")

    public String easterEgg(org.springframework.ui.Model model) {

        model.addAttribute("pageTitle", "Easter egg");

        return "easter-egg";

    }


    /**

     * Optionnel : redirige /index vers /login également

     */

    @GetMapping("/index")

    public String redirectIndexToLogin() {

        return "redirect:/login";

    }

}

