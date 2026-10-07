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
    public List<Movie> browseMovies() {
        return movieProvider.getPopularMovies();
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

}