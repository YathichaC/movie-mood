package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.WatchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, UUID> {
    List<WatchHistory> findByUserId(UUID userId);

    long countByUserId(UUID userId);

    Page<WatchHistory> findByUserId(UUID userId, Pageable pageable);

    Optional<WatchHistory> findByUserIdAndTmdbMovieId(UUID userId, String tmdbMovieId);

    void deleteByUserIdAndTmdbMovieId(UUID userId, String tmdbMovieId);

    void deleteByUserId(UUID userId);
}