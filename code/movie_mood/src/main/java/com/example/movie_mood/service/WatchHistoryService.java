package com.example.movie_mood.service;

import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WatchHistoryService {
    WatchHistoryResponse recordWatchedMovie(UUID userId, WatchHistoryRequest request);
    List<WatchHistoryResponse> getUserWatchHistory(UUID userId);

    boolean isMovieWatched(UUID userId, String tmdbMovieId);
    void removeWatchedMovie(UUID userId, String tmdbMovieId);
    void clearUserHistory(UUID userId);

    Page<WatchHistoryResponse> getUserWatchHistory(UUID userId, Pageable pageable);
}