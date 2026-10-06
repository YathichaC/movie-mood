package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GenreService {

    Genre createGenre(Genre genre);

    Page<Genre> getAllGenres(Pageable pageable);

    Genre getGenreById(String genreId);

    Genre updateGenre(String genreId, Genre genre);

    void deleteGenre(String genreId);
}