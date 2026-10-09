package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.UserDislikedGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserDislikedGenreRepository
        extends JpaRepository<UserDislikedGenre, UUID> {

    List<UserDislikedGenre> findByUserUserId(UUID userId);

    void deleteByUserUserId(UUID userId);

    boolean existsByUserUserIdAndGenreGenreId(
            UUID userId,
            String genreId
    );
}