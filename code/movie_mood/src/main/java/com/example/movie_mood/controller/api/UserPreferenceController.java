package com.example.movie_mood.controller.api;

import com.example.movie_mood.service.UserPreferenceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/preferences")
public class UserPreferenceController {
        private final UserPreferenceService userPreferenceService;

        public UserPreferenceController(
                        UserPreferenceService userPreferenceService) {
                this.userPreferenceService = userPreferenceService;
        }

        @GetMapping("/disliked-genres")
        public ResponseEntity<?> getDislikedGenres(
                        @PathVariable Integer userId, HttpSession session) {
                Integer currentUserId = (Integer) session.getAttribute("USER_ID");
                if (currentUserId == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(Map.of("message", "Please login first"));
                }
                if (!currentUserId.equals(userId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                                        Map.of("message", "Access denied"));
                }
                try {
                        List<Integer> genreIds = userPreferenceService
                                        .getDislikedGenreIds(userId);
                        return ResponseEntity.ok(
                                        Map.of(
                                                        "userId", userId,
                                                        "dislikedGenreIds", genreIds));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(
                                        Map.of("message", e.getMessage()));
                }
        }

        @PutMapping("/disliked-genres")
        public ResponseEntity<?> updateDislikedGenres(
                        @PathVariable Integer userId,
                        @RequestBody List<Integer> genreIds, HttpSession session) {
                Integer currentUserId = (Integer) session.getAttribute("USER_ID");
                if (currentUserId == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                        Map.of("message", "Please login first"));
                }
                if (!currentUserId.equals(userId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                                        Map.of("message", "Access denied"));
                }
                if (genreIds == null) {
                        return ResponseEntity.badRequest().body(
                                        Map.of("message", "Genre list cannot be null"));
                }
                try {
                        List<Integer> updatedGenreIds = userPreferenceService
                                        .updateDislikedGenres(userId, genreIds);
                        return ResponseEntity.ok(
                                        Map.of(
                                                        "message", "Preferences updated successfully",
                                                        "userId", userId,
                                                        "dislikedGenreIds", updatedGenreIds));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(
                                        Map.of("message", e.getMessage()));
                }
        }
}