package com.example.movie_mood.controller.web;

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

    @GetMapping("/auth/reset-password")
public String resetPassword(
        @RequestParam(value = "token", required = false) String token,
        Model model) {
    model.addAttribute("token", token);
    return "auth/changepassword";
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
            @RequestParam(value = "mood", required = false) String mood,
            Model model) {
        if (mood == null || mood.isBlank()) {
            return "recommendations/mood-selection";
        }
        model.addAttribute("mood", mood);
        return "recommendations/recommendations";
    }

    @GetMapping("/playlist")
    public String playlist() {
        return "Playlist/playlist";
    }

    @GetMapping("/playlist/movielist")
    public String movieList() {
        return "Playlist/movielist";
    }

    @GetMapping("/history")
    public String history() {
        return "history/movie-history";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile/profile";
    }

    @GetMapping("/auth/change-password")
    public String changePassword() {
        return "auth/changepassword";
    }

    @GetMapping("/")
    public String landingPage() {
        return "landing-page";
    }
}
