package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.UserDislikedGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDislikedGenreRepository
        extends JpaRepository<UserDislikedGenre, Long> {

    List<UserDislikedGenre> findByUserUserId(Integer userId);

    void deleteByUserUserId(Integer userId);

    boolean existsByUserUserIdAndGenreGenreId(
            Integer userId,
            Integer genreId
    );
}