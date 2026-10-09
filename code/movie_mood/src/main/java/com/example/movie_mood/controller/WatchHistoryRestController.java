package com.example.movie_mood.controller;

import com.example.movie_mood.dto.WatchHistoryMovieResponse;
import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import com.example.movie_mood.service.WatchHistoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/history")
@SecurityRequirement(name = "bearerAuth")
public class WatchHistoryRestController {

    private static final Logger log = LoggerFactory.getLogger(WatchHistoryRestController.class);
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
        long requestStartNanos = System.nanoTime();
        int safePage = Math.max(1, page);
        int pageIndex = Math.max(0, safePage - 1);
        Pageable pageable = PageRequest.of(pageIndex, DEFAULT_PAGE_SIZE);

        try {
            UUID userId = getCurrentUserId(authentication);

            long historyQueryStartNanos = System.nanoTime();
            List<WatchHistoryMovieResponse> history = watchHistoryService.getUserMovieHistory(userId, pageable);
            long historyQueryDurationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - historyQueryStartNanos);
            log.info("GET /api/v1/history paginated-history-query durationMs={} page={} pageSize={} resultCount={}",
                    historyQueryDurationMs, safePage, DEFAULT_PAGE_SIZE, history != null ? history.size() : 0);

            long countQueryStartNanos = System.nanoTime();
            long totalItems = watchHistoryService.getUserMovieHistoryCount(userId);
            long countQueryDurationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - countQueryStartNanos);
            log.info("GET /api/v1/history total-history-count durationMs={} page={} totalItems={}",
                    countQueryDurationMs, safePage, totalItems);

            int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / DEFAULT_PAGE_SIZE));

            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(totalItems))
                    .header("X-Total-Pages", String.valueOf(totalPages))
                    .header("X-Page-Size", String.valueOf(DEFAULT_PAGE_SIZE))
                    .header("X-Current-Page", String.valueOf(safePage))
                    .body(history);
        } finally {
            long totalDurationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestStartNanos);
            log.info("GET /api/v1/history totalRequestDurationMs={} page={} pageSize={}",
                    totalDurationMs, safePage, DEFAULT_PAGE_SIZE);
        }
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