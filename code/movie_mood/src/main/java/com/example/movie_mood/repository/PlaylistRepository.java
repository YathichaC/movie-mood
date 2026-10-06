package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    List<Playlist> findByUserId(UUID userId);
    Optional<Playlist> findByPlaylistIdAndUserId(
            UUID playlistId,
            UUID userId);
}