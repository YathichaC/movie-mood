package com.example.movie_mood.mapper;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.dto.response.MovieBatchResponse;
import com.example.movie_mood.dto.response.MovieResponse;
import org.springframework.stereotype.Component;
import com.example.movie_mood.domain.model.MoviePage;
import com.example.movie_mood.dto.response.MoviePageResponse;

import java.util.List;

@Component
public class MovieMapper {

    public MovieResponse toResponse(Movie movie) {

        if (movie == null) {
            return null;
        }

        List<String> genreIds = movie.getGenreIds() == null
                ? List.of()
                : movie.getGenreIds().stream()
                        .map(String::valueOf)
                        .toList();

        MovieResponse response = new MovieResponse(
                String.valueOf(movie.getTmdbMovieId()),
                movie.getTitle(),
                movie.getSynopsis(),
                movie.getRating(),
                movie.getReleaseDate(),
                genreIds);

        response.setPosterPath(movie.getPosterPath());
        response.setBackdropPath(movie.getBackdropPath());

        response.setGenres(
                movie.getGenres() == null
                        ? List.of()
                        : movie.getGenres());

        return response;
    }

    public List<MovieResponse> toResponseList(List<Movie> movies) {

        if (movies == null) {
            return List.of();
        }

        return movies.stream()
                .map(this::toResponse)
                .toList();
    }

    public MovieBatchResponse toBatchResponse(Movie movie) {
        if (movie == null) {
            return null;
        }

        return new MovieBatchResponse(
                String.valueOf(movie.getTmdbMovieId()),
                movie.getPosterPath(),
                movie.getTitle());
    }

    public List<MovieBatchResponse> toBatchResponseList(List<Movie> movies) {
        if (movies == null) {
            return List.of();
        }

        return movies.stream()
                .map(this::toBatchResponse)
                .toList();
    }
    
    public MoviePageResponse toPageResponse(MoviePage moviePage) {
    if (moviePage == null) {
        return new MoviePageResponse(
                List.of(),
                0,
                0,
                0
        );
    }

    return new MoviePageResponse(
            toResponseList(moviePage.getMovies()),
            moviePage.getPage(),
            moviePage.getTotalPages(),
            moviePage.getTotalResults()
    );
}
}