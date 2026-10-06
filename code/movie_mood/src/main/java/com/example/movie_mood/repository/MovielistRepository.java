package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.Movielist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovielistRepository extends JpaRepository<Movielist, UUID> {

    List<Movielist> findByPlaylist_PlaylistId(UUID playlistId);

    boolean existsByPlaylist_PlaylistIdAndTmdbMovieId(
            UUID playlistId,
            String tmdbMovieId
    );

    Optional<Movielist> findByPlaylist_PlaylistIdAndTmdbMovieId(
            UUID playlistId,
            String tmdbMovieId
    );

    void deleteByPlaylist_PlaylistIdAndTmdbMovieId(
            UUID playlistId,
            String tmdbMovieId
    );
}