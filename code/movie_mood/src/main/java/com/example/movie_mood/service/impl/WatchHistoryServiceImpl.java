package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.entity.WatchHistory;
import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import com.example.movie_mood.repository.WatchHistoryRepository;
import com.example.movie_mood.service.WatchHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatchHistoryServiceImpl implements WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;

    public WatchHistoryServiceImpl(WatchHistoryRepository watchHistoryRepository) {
        this.watchHistoryRepository = watchHistoryRepository;
    }

    @Override
    @Transactional
    public WatchHistoryResponse recordWatchedMovie(Long userId, WatchHistoryRequest request) {
        WatchHistory history = watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, request.getTmdbMovieId())
                .map(existing -> {
                    existing.setWatchedAt(LocalDateTime.now());
                    return existing;
                })
                .orElseGet(() -> new WatchHistory(
                        userId,
                        request.getTmdbMovieId(),
                        request.getTitle(),
                        request.getPosterPath()
                ));

        WatchHistory saved = watchHistoryRepository.save(history);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchHistoryResponse> getUserWatchHistory(Long userId) {
    return watchHistoryRepository.findByUserIdOrderByTitleAsc(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
}

    @Override
    @Transactional(readOnly = true)
    public boolean isMovieWatched(Long userId, Long tmdbMovieId) {
        return watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, tmdbMovieId).isPresent();
    }

    @Override
    @Transactional
    public void removeWatchedMovie(Long userId, Long tmdbMovieId) {
        watchHistoryRepository.deleteByUserIdAndTmdbMovieId(userId, tmdbMovieId);
    }

    @Override
    @Transactional
    public void clearUserHistory(Long userId) {
        watchHistoryRepository.deleteByUserId(userId);
    }

    private WatchHistoryResponse mapToResponse(WatchHistory history) {
        return new WatchHistoryResponse(
                history.getId(),
                history.getTmdbMovieId(),
                history.getTitle(),
                history.getPosterPath(),
                history.getWatchedAt()
        );
    }
}