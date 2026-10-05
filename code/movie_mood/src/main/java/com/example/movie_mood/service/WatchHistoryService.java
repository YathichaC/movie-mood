package com.example.movie_mood.service;

import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;

import java.util.List;

public interface WatchHistoryService {
    WatchHistoryResponse recordWatchedMovie(Long userId, WatchHistoryRequest request);

    List<WatchHistoryResponse> getUserWatchHistory(Long userId);

    boolean isMovieWatched(Long userId, Long tmdbMovieId);
    void removeWatchedMovie(Long userId, Long tmdbMovieId);

    void clearUserHistory(Long userId);
}