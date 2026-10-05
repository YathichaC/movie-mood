package com.example.movie_mood.controller;

import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import com.example.movie_mood.service.WatchHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<WatchHistoryResponse>> getHistory() {
        Long currentUserId = 1L; // Mock user ID ไว้สำหรับรอบพัฒนา
        List<WatchHistoryResponse> history = watchHistoryService.getUserWatchHistory(currentUserId);
        return ResponseEntity.ok(history);
    }

    // บันทึกว่าดูหนังแล้ว
    @PostMapping
    public ResponseEntity<WatchHistoryResponse> recordWatched(@RequestBody WatchHistoryRequest request) {
        Long currentUserId = 1L;
        WatchHistoryResponse response = watchHistoryService.recordWatchedMovie(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // เช็คว่าหนังเรื่องนี้เคยดูหรือยัง
    @GetMapping("/check/{tmdbMovieId}")
    public ResponseEntity<Map<String, Boolean>> checkWatched(@PathVariable Long tmdbMovieId) {
        Long currentUserId = 1L;
        boolean watched = watchHistoryService.isMovieWatched(currentUserId, tmdbMovieId);
        return ResponseEntity.ok(Map.of("watched", watched));
    }

    // ลบหนังออกจากประวัติ
    @DeleteMapping("/{tmdbMovieId}")
    public ResponseEntity<Void> removeWatched(@PathVariable Long tmdbMovieId) {
        Long currentUserId = 1L;
        watchHistoryService.removeWatchedMovie(currentUserId, tmdbMovieId);
        return ResponseEntity.noContent().build();
    }

    // ล้างประวัติทั้งหมด
    @DeleteMapping
    public ResponseEntity<Void> clearHistory() {
        Long currentUserId = 1L;
        watchHistoryService.clearUserHistory(currentUserId);
        return ResponseEntity.noContent().build();
    }
}