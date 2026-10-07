package com.example.movie_mood.controller;

import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import com.example.movie_mood.service.WatchHistoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/history")
@SecurityRequirement(name = "bearerAuth")
public class WatchHistoryRestController {

    private final WatchHistoryService watchHistoryService;

    public WatchHistoryRestController(WatchHistoryService watchHistoryService) {
        this.watchHistoryService = watchHistoryService;
    }

    @GetMapping
    public ResponseEntity<?> getHistory(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);

        List<WatchHistoryResponse> history = watchHistoryService.getUserWatchHistory(userId);

        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<?> recordWatched(
            @RequestBody WatchHistoryRequest request,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);

        WatchHistoryResponse response = watchHistoryService.recordWatchedMovie(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/check/{tmdbMovieId}")
    public ResponseEntity<?> checkWatched(
            @PathVariable String tmdbMovieId,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);

        boolean watched = watchHistoryService.isMovieWatched(userId, tmdbMovieId);

        return ResponseEntity.ok(Map.of("watched", watched));
    }

    @DeleteMapping("/{tmdbMovieId}")
    public ResponseEntity<?> removeWatched(
            @PathVariable String tmdbMovieId,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);

        watchHistoryService.removeWatchedMovie(userId, tmdbMovieId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> clearHistory(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);

        watchHistoryService.clearUserHistory(userId);

        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}