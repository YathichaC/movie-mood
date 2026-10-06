package com.example.movie_mood.controller.api;

import com.example.movie_mood.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/preferences")
@SecurityRequirement(name = "bearerAuth")
public class UserPreferenceController {

        private final UserPreferenceService userPreferenceService;

        public UserPreferenceController(
                        UserPreferenceService userPreferenceService) {
                this.userPreferenceService = userPreferenceService;
        }

        @GetMapping("/disliked-genres")
        public ResponseEntity<?> getDislikedGenres(
                        @PathVariable UUID userId,
                        Authentication authentication) {

                UUID currentUserId = getCurrentUserId(authentication);

                if (!currentUserId.equals(userId)) {
                        return ResponseEntity.status(403).body(
                                        Map.of("message", "Access denied"));
                }

                try {
                        List<String> genreIds = userPreferenceService.getDislikedGenreIds(userId);

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
                        @PathVariable UUID userId,
                        @RequestBody List<String> genreIds,
                        Authentication authentication) {

                UUID currentUserId = getCurrentUserId(authentication);

                if (!currentUserId.equals(userId)) {
                        return ResponseEntity.status(403).body(
                                        Map.of("message", "Access denied"));
                }

                if (genreIds == null) {
                        return ResponseEntity.badRequest().body(
                                        Map.of("message", "Genre list cannot be null"));
                }

                try {
                        List<String> updatedGenreIds = userPreferenceService.updateDislikedGenres(
                                        userId,
                                        genreIds);

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

        private UUID getCurrentUserId(Authentication authentication) {
                return UUID.fromString(authentication.getName());
        }
}