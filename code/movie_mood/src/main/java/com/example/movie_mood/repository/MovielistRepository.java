package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.Movielist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovielistRepository extends JpaRepository<Movielist, UUID> {

    @Query("select m.playlist.playlistId from Movielist m where m.tmdbMovieId = :tmdbMovieId and m.playlist.userId = :userId")
    List<UUID> findPlaylistIdsByTmdbMovieIdAndUserId(
            @Param("tmdbMovieId") String tmdbMovieId,
            @Param("userId") UUID userId
    );

    List<Movielist> findByPlaylist_PlaylistId(UUID playlistId);

    boolean existsByPlaylist_PlaylistIdAndTmdbMovieId(
            UUID playlistId,
            String tmdbMovieId
    );

    Optional<Movielist> findByPlaylist_PlaylistIdAndTmdbMovieId(
            UUID playlistId,
            String tmdbMovieId
    );

    void deleteByPlaylist_PlaylistId(UUID playlistId);

    void deleteByPlaylist_PlaylistIdAndTmdbMovieId(
            UUID playlistId,
            String tmdbMovieId
    );
}