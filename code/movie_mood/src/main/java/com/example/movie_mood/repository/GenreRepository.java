package com.example.movie_mood.repository;

import com.example.movie_mood.domain.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
