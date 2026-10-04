package com.example.movie_mood.mapper;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.dto.response.MovieResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieMapper {

    public MovieResponse toResponse(Movie movie) {

        if (movie == null) {
            return null;
        }

        return new MovieResponse(
                movie.getTmdbMovieId(),
                movie.getTitle(),
                movie.getSynopsis(),
                movie.getRating(),
                movie.getReleaseDate(),
                movie.getGenreIds()
        );
    }

    public List<MovieResponse> toResponseList(
            List<Movie> movies) {

        if (movies == null) {
            return List.of();
        }

        return movies.stream()
                .map(this::toResponse)
                .toList();
    }
}