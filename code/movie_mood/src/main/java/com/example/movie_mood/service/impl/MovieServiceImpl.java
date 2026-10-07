package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.service.MovieService;
import org.springframework.stereotype.Service;
import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.repository.GenreRepository;
import com.example.movie_mood.domain.model.Video;
import com.example.movie_mood.domain.entity.Genre;
import java.util.Comparator;
import com.example.movie_mood.domain.model.MoviePage;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    private static final List<String> ALLOWED_DISCOVER_SORT_VALUES = List.of(
            "rating_desc",
            "alphabet_asc",
            "release_desc",
            "release_asc");

    private final MovieProvider movieProvider;
    private final MoodGenreMapper moodGenreMapper;
    private final GenreRepository genreRepository;

    public MovieServiceImpl(
            MovieProvider movieProvider,
            MoodGenreMapper moodGenreMapper,
            GenreRepository genreRepository) {
        this.movieProvider = movieProvider;
        this.moodGenreMapper = moodGenreMapper;
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Movie> filterMoviesByMood(Mood mood) {
        List<Integer> genreIds = moodGenreMapper.getGenreIds(mood);

        return movieProvider.discoverMoviesByGenres(genreIds);
    }

    @Override
    public MoviePage browseMovies(int page) {
        return movieProvider.getPopularMovies(page);
    }

    @Override
    public MoviePage searchMovies(String keyword, int page) {
        return movieProvider.searchMovies(keyword, page);
    }

    @Override
    public Movie getMovieDetails(String tmdbMovieId) {
        Movie movie = movieProvider.getMovie(tmdbMovieId);

        if (movie == null || movie.getGenreIds() == null) {
            return movie;
        }

        List<String> genreNames = movie.getGenreIds().stream()
                .map(String::valueOf)
                .map(genreRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(Genre::getGenreName)
                .toList();

        movie.setGenres(genreNames);

        return movie;
    }

    @Override
    public List<Movie> filterMoviesByGenre(Integer genreId) {
        return movieProvider.discoverMoviesByGenres(
                List.of(genreId));
    }

    @Override
    public Video getMovieTrailer(String tmdbMovieId) {

        List<Video> videos = movieProvider.getMovieVideos(tmdbMovieId);

        return videos.stream()
                .filter(video -> "YouTube".equalsIgnoreCase(video.getSite()))
                .filter(video -> "Trailer".equalsIgnoreCase(video.getType()))
                .sorted(
                        Comparator.comparing(
                                Video::isOfficial).reversed())
                .findFirst()
                .orElse(null);
    }

    @Override
    public MoviePage discoverMovies(
            Integer genreId,
            Integer startYear,
            Integer endYear,
            Double minRating,
            String sortBy,
            int page) {

        if (startYear != null
                && endYear != null
                && startYear > endYear) {
            throw new IllegalArgumentException(
                    "startYear must not be greater than endYear");
        }

        if (sortBy != null
                && !sortBy.isBlank()
                && !ALLOWED_DISCOVER_SORT_VALUES.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sortBy value");
        }

        return movieProvider.discoverMovies(
                genreId,
                startYear,
                endYear,
                minRating,
                sortBy,
                page);
    }

}
