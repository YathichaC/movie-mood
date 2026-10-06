package com.example.movie_mood.controller.api;

import com.example.movie_mood.service.UserPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @PathVariable Integer userId) {

        try {
            List<Integer> genreIds =
                    userPreferenceService
                            .getDislikedGenreIds(userId);

            return ResponseEntity.ok(
                    Map.of(
                            "userId", userId,
                            "dislikedGenreIds", genreIds
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    @PutMapping("/disliked-genres")
    public ResponseEntity<?> updateDislikedGenres(
            @PathVariable Integer userId,
            @RequestBody List<Integer> genreIds) {

        try {
            List<Integer> updatedGenreIds =
                    userPreferenceService
                            .updateDislikedGenres(
                                    userId,
                                    genreIds
                            );

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Preferences updated successfully",
                            "userId", userId,
                            "dislikedGenreIds", updatedGenreIds
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }
}