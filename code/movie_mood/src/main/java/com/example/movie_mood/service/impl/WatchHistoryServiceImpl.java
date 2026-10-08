package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.entity.WatchHistory;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.dto.WatchHistoryMovieResponse;
import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import com.example.movie_mood.repository.WatchHistoryRepository;
import com.example.movie_mood.service.MovieService;
import com.example.movie_mood.service.WatchHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class WatchHistoryServiceImpl implements WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;
    private final MovieService movieService;

    public WatchHistoryServiceImpl(WatchHistoryRepository watchHistoryRepository, MovieService movieService) {
        this.watchHistoryRepository = watchHistoryRepository;
        this.movieService = movieService;
    }

    @Override
    @Transactional
    public WatchHistoryResponse recordWatchedMovie(UUID userId, WatchHistoryRequest request) {
        WatchHistory history = watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, request.getTmdbMovieId())
                .orElseGet(() -> new WatchHistory(
                        userId,
                        request.getTmdbMovieId()));
        WatchHistory saved = watchHistoryRepository.save(history);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public WatchHistoryResponse toggleWatchedMovie(UUID userId, String tmdbMovieId) {
        return watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, tmdbMovieId)
                .map(existing -> {
                    watchHistoryRepository.delete(existing);
                    return new WatchHistoryResponse(existing.getHistoryId(), existing.getTmdbMovieId(), false);
                })
                .orElseGet(() -> {
                    WatchHistory saved = watchHistoryRepository.save(new WatchHistory(userId, tmdbMovieId));
                    return new WatchHistoryResponse(saved.getHistoryId(), saved.getTmdbMovieId(), true);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchHistoryResponse> getUserWatchHistory(UUID userId) {
        return watchHistoryRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchHistoryMovieResponse> getUserMovieHistory(UUID userId, Pageable pageable) {
        return watchHistoryRepository.findByUserId(userId, pageable)
                .getContent()
                .stream()
                .map(this::mapToMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMovieWatched(UUID userId, String tmdbMovieId) {
        return watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, tmdbMovieId).isPresent();
    }

    @Override
    @Transactional
    public void removeWatchedMovie(UUID userId, String tmdbMovieId) {
        watchHistoryRepository.deleteByUserIdAndTmdbMovieId(userId, tmdbMovieId);
    }

    @Override
    @Transactional
    public void clearUserHistory(UUID userId) {
        watchHistoryRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WatchHistoryResponse> getUserWatchHistory(UUID userId, Pageable pageable) {
        return watchHistoryRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    private WatchHistoryResponse mapToResponse(WatchHistory history) {
        return new WatchHistoryResponse(
                history.getHistoryId(),
                history.getTmdbMovieId());
    }

    private WatchHistoryMovieResponse mapToMovieResponse(WatchHistory history) {
        try {
            Movie movie = movieService.getMovieDetails(history.getTmdbMovieId());
            return new WatchHistoryMovieResponse(
                    movie.getTmdbMovieId(),
                    movie.getTitle(),
                    movie.getPosterPath(),
                    movie.getReleaseDate(),
                    movie.getRating());
        } catch (Exception ex) {
            return new WatchHistoryMovieResponse(
                    history.getTmdbMovieId(),
                    "Movie #" + history.getTmdbMovieId(),
                    null,
                    null,
                    null);
        }
    }
}