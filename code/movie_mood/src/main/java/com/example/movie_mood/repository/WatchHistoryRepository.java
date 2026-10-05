package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {

    List<WatchHistory> findByUserIdOrderByTitleAsc(Long userId);

    Optional<WatchHistory> findByUserIdAndTmdbMovieId(Long userId, Long tmdbMovieId);
    
    void deleteByUserIdAndTmdbMovieId(Long userId, Long tmdbMovieId);
    void deleteByUserId(Long userId);
}