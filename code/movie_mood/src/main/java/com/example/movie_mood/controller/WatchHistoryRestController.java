package com.example.movie_mood.controller;

import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import com.example.movie_mood.service.WatchHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/history")
public class WatchHistoryRestController {

    private final WatchHistoryService watchHistoryService;

    public WatchHistoryRestController(WatchHistoryService watchHistoryService) {
        this.watchHistoryService = watchHistoryService;
    }

    @GetMapping
    public ResponseEntity<?> getHistory(HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        List<WatchHistoryResponse> history = watchHistoryService.getUserWatchHistory(userId);
        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<?> recordWatched(@RequestBody WatchHistoryRequest request, HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        WatchHistoryResponse response = watchHistoryService.recordWatchedMovie(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/check/{tmdbMovieId}")
    public ResponseEntity<?> checkWatched(@PathVariable String tmdbMovieId, HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        boolean watched = watchHistoryService.isMovieWatched(userId, tmdbMovieId);
        return ResponseEntity.ok(Map.of("watched", watched));
    }

    @DeleteMapping("/{tmdbMovieId}")
    public ResponseEntity<?> removeWatched(@PathVariable String tmdbMovieId, HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        watchHistoryService.removeWatchedMovie(userId, tmdbMovieId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> clearHistory(HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        watchHistoryService.clearUserHistory(userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId(HttpSession session) {
        return (UUID) session.getAttribute("USER_ID");
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Please login first"));
    }
}