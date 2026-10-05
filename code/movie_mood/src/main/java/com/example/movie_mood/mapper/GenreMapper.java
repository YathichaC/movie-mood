package com.example.movie_mood.mapper;

import com.example.movie_mood.domain.entity.Genre;
import com.example.movie_mood.dto.GenreRequest;
import com.example.movie_mood.dto.response.GenreResponse;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public Genre toEntity(GenreRequest request) {
        return new Genre(
                request.getGenreId(),
                request.getGenreName()
        );
    }

    public GenreResponse toResponse(Genre genre) {
        return new GenreResponse(
                genre.getGenreId(),
                genre.getGenreName()
        );
    }
}