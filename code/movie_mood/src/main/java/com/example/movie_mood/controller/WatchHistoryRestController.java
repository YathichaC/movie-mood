package com.example.movie_mood.controller;

import com.example.movie_mood.dto.WatchHistoryMovieResponse;
import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import com.example.movie_mood.service.WatchHistoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/history")
@SecurityRequirement(name = "bearerAuth")
public class WatchHistoryRestController {

    private static final int DEFAULT_PAGE_SIZE = 15;

    private final WatchHistoryService watchHistoryService;

    public WatchHistoryRestController(WatchHistoryService watchHistoryService) {
        this.watchHistoryService = watchHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<WatchHistoryMovieResponse>> getHistory(
            @RequestParam(defaultValue = "1") int page,
            Authentication authentication
    ) {
        UUID userId = getCurrentUserId(authentication);
        int pageIndex = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageIndex, DEFAULT_PAGE_SIZE);

        List<WatchHistoryMovieResponse> response = watchHistoryService.getUserMovieHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/toggle/{tmdbMovieId}")
    public ResponseEntity<WatchHistoryResponse> toggleWatched(
            @PathVariable String tmdbMovieId,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);
        WatchHistoryResponse response = watchHistoryService.toggleWatchedMovie(userId, tmdbMovieId);

        return ResponseEntity.ok(response);
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