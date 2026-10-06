package com.example.movie_mood.service;

import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import java.util.UUID;
import java.util.List;

public interface WatchHistoryService {
    WatchHistoryResponse recordWatchedMovie(UUID userId, WatchHistoryRequest request);

    List<WatchHistoryResponse> getUserWatchHistory(UUID userId);

    boolean isMovieWatched(UUID userId, String tmdbMovieId);
    void removeWatchedMovie(UUID userId, String tmdbMovieId);

    void clearUserHistory(UUID userId);
}