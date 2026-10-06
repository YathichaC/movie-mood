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

@RestController
@RequestMapping("/api/history")
public class WatchHistoryRestController {

    private final WatchHistoryService watchHistoryService;

    public WatchHistoryRestController(WatchHistoryService watchHistoryService) {
        this.watchHistoryService = watchHistoryService;
    }

    @GetMapping
    public ResponseEntity<?> getHistory(HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        List<WatchHistoryResponse> history = watchHistoryService.getUserWatchHistory(userId.longValue());
        return ResponseEntity.ok(history);
    }

    // บันทึกว่าดูหนังแล้ว
    @PostMapping
    public ResponseEntity<?> recordWatched(@RequestBody WatchHistoryRequest request, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        WatchHistoryResponse response = watchHistoryService.recordWatchedMovie(userId.longValue(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // เช็คว่าหนังเรื่องนี้เคยดูหรือยัง
    @GetMapping("/check/{tmdbMovieId}")
    public ResponseEntity<?> checkWatched(@PathVariable Long tmdbMovieId, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        boolean watched = watchHistoryService.isMovieWatched(userId.longValue(), tmdbMovieId);
        return ResponseEntity.ok(Map.of("watched", watched));
    }

    // ลบหนังออกจากประวัติ
    @DeleteMapping("/{tmdbMovieId}")
    public ResponseEntity<?> removeWatched(@PathVariable Long tmdbMovieId, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        watchHistoryService.removeWatchedMovie(userId.longValue(), tmdbMovieId);
        return ResponseEntity.noContent().build();
    }

    // ล้างประวัติทั้งหมด
    @DeleteMapping
    public ResponseEntity<?> clearHistory(HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        watchHistoryService.clearUserHistory(userId.longValue());
        return ResponseEntity.noContent().build();
    }

    private Integer getCurrentUserId(HttpSession session) {
        return (Integer) session.getAttribute("USER_ID");
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Please login first"));
    }
}