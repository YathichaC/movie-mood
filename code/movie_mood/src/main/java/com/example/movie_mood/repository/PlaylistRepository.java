package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    List<Playlist> findByUserId(UUID userId);

    @Query("""
        select p.playlistId, p.playlistName, count(m.id)
        from Playlist p
        left join p.items m
        where p.userId = :userId
        group by p.playlistId, p.playlistName
        order by p.playlistName asc
        """)
    List<Object[]> findPickerDataByUserId(@Param("userId") UUID userId);

    Optional<Playlist> findByPlaylistIdAndUserId(
            UUID playlistId,
            UUID userId);
}