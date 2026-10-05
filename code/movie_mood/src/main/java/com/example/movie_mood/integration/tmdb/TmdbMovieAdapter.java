package com.example.movie_mood.integration.tmdb;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.dto.TmdbGenreResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieListResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class TmdbMovieAdapter implements MovieProvider {

    private final TmdbRestClient tmdbRestClient;

    public TmdbMovieAdapter(TmdbRestClient tmdbRestClient) {
        this.tmdbRestClient = tmdbRestClient;
    }

    @Override
    public List<Movie> getPopularMovies() {
        TmdbMovieListResponse response = tmdbRestClient.getPopularMovies();

        if (response == null || response.getResults() == null) {
            return Collections.emptyList();
        }

        return response.getResults()
                .stream()
                .map(this::toMovie)
                .toList();
    }

    @Override
    public List<Movie> searchMovies(String keyword) {
        TmdbMovieListResponse response = tmdbRestClient.searchMovies(keyword);

        if (response == null || response.getResults() == null) {
            return Collections.emptyList();
        }

        return response.getResults()
                .stream()
                .map(this::toMovie)
                .toList();
    }

    @Override
    public Movie getMovie(Long tmdbMovieId) {
        return toMovie(tmdbRestClient.getMovie(tmdbMovieId));
    }

    private Movie toMovie(TmdbMovieResponse response) {
        if (response == null) {
            return null;
        }

        Movie movie = new Movie();

        movie.setTmdbMovieId(response.getId());
        movie.setTitle(response.getTitle());
        movie.setSynopsis(response.getOverview());
        movie.setRating(response.getVoteAverage());

        if (response.getGenreIds() != null) {
            movie.setGenreIds(response.getGenreIds());
        } else if (response.getGenres() != null) {
            movie.setGenreIds(
                    response.getGenres()
                            .stream()
                            .map(TmdbGenreResponse::getId)
                            .toList()
            );
        }

        movie.setPosterPath(response.getPosterPath());
        movie.setBackdropPath(response.getBackdropPath());

        if (response.getReleaseDate() != null
                && !response.getReleaseDate().isBlank()) {
            movie.setReleaseDate(
                    LocalDate.parse(response.getReleaseDate())
            );
        }

        return movie;
    }

    @Override
    public List<Movie> discoverMoviesByGenres(List<Integer> genreIds) {

        TmdbMovieListResponse response =
                tmdbRestClient.discoverMoviesByGenres(genreIds);

        if (response == null || response.getResults() == null) {
            return Collections.emptyList();
        }

        return response.getResults()
                .stream()
                .map(this::toMovie)
                .toList();
    }
}