package com.example.movie_mood.integration.tmdb.proxy;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.domain.model.Video;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.integration.tmdb.TmdbMovieAdapter;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.example.movie_mood.domain.model.MoviePage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Primary
public class CachingMovieServiceProxy implements MovieProvider {

    private final TmdbMovieAdapter movieAdapter;

    private final Map<String, Movie> movieCache = new ConcurrentHashMap<>();

    public CachingMovieServiceProxy(
            TmdbMovieAdapter movieAdapter) {
        this.movieAdapter = movieAdapter;
    }

    @Override
    public List<Movie> getPopularMovies() {
        return movieAdapter.getPopularMovies();
    }

    @Override
    public MoviePage searchMovies(String keyword, int page) {
        return movieAdapter.searchMovies(keyword, page);
    }

    @Override
    public Movie getMovie(String tmdbMovieId) {

        return movieCache.computeIfAbsent(
                tmdbMovieId,
                movieAdapter::getMovie);
    }

    @Override
    public List<Video> getMovieVideos(String tmdbMovieId) {
        return movieAdapter.getMovieVideos(tmdbMovieId);
    }

    @Override
    public List<Movie> discoverMoviesByGenres(
            List<Integer> genreIds) {

        return movieAdapter.discoverMoviesByGenres(genreIds);
    }
}