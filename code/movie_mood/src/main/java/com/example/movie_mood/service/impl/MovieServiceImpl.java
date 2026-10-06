package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.service.MovieService;
import org.springframework.stereotype.Service;
import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.mapper.MoodGenreMapper;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieProvider movieProvider;
    private final MoodGenreMapper moodGenreMapper;

    public MovieServiceImpl(
            MovieProvider movieProvider,
            MoodGenreMapper moodGenreMapper) {
        this.movieProvider = movieProvider;
        this.moodGenreMapper = moodGenreMapper;
    }

    @Override
    public List<Movie> filterMoviesByMood(Mood mood) {
        List<Integer> genreIds = moodGenreMapper.getGenreIds(mood);

        return movieProvider.discoverMoviesByGenres(genreIds);
    }

    @Override
    public List<Movie> browseMovies() {
        return movieProvider.getPopularMovies();
    }

    @Override
    public List<Movie> searchMovies(String keyword) {
        return movieProvider.searchMovies(keyword);
    }

    @Override
    public Movie getMovieDetails(String tmdbMovieId) {
        return movieProvider.getMovie(tmdbMovieId);
    }

    @Override
    public List<Movie> filterMoviesByGenre(Integer genreId) {
        return movieProvider.discoverMoviesByGenres(
                List.of(genreId));
    }
}