package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.PlaylistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Long> {

    List<PlaylistItem> findByPlaylistIdOrderByAddedAtDesc(Long playlistId);

    boolean existsByPlaylistIdAndTmdbMovieId(Long playlistId, Long tmdbMovieId);

    Optional<PlaylistItem> findByPlaylistIdAndTmdbMovieId(Long playlistId, Long tmdbMovieId);

    void deleteByPlaylistIdAndTmdbMovieId(Long playlistId, Long tmdbMovieId);
}