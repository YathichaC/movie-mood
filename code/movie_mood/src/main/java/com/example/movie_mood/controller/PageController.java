package com.example.movie_mood.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @GetMapping("/auth/password-reset")
    public String passwordReset() {
        return "auth/password-email";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "docs/privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "docs/terms-of-service";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/movie/detail")
    public String movieDetail() {
        return "movie/detail";
    }

    @GetMapping("/movie/search")
    public String movieSearch() {
        return "movie/search";
    }

    @GetMapping("/recommendations")
    public String recommendations(
            @RequestParam(value = "id", required = false) String mood,
            Model model) {

        if (mood == null || mood.isBlank()) {
            // ไม่มี id → หน้าเลือก Mood
            return "recommendations/mood-selection";
        }

        // มี id → หน้าผล Recommendation
        model.addAttribute("mood", mood);
        return "recommendations/recommendations";
    }
}
