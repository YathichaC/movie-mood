package com.example.movie_mood.integration.tmdb;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.domain.model.MoviePage;
import com.example.movie_mood.integration.tmdb.dto.TmdbGenreResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieListResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.example.movie_mood.domain.model.Video;
import com.example.movie_mood.integration.tmdb.dto.TmdbVideoListResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbVideoResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbImageResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieImagesResponse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TmdbMovieAdapter implements MovieProvider {

    private static final Logger log = LoggerFactory.getLogger(TmdbMovieAdapter.class);

    private final TmdbRestClient tmdbRestClient;
    private static final int TMDB_MAX_PAGE = 500;

    public TmdbMovieAdapter(TmdbRestClient tmdbRestClient) {
        this.tmdbRestClient = tmdbRestClient;
    }

    @Override
    public MoviePage getPopularMovies(int page) {
        TmdbMovieListResponse response = tmdbRestClient.getPopularMovies(page);

        if (response == null || response.getResults() == null) {
            return new MoviePage(
                    Collections.emptyList(),
                    page,
                    0,
                    0);
        }

        List<Movie> movies = response.getResults()
                .stream()
                .map(this::toMovie)
                .toList();

        return new MoviePage(
                movies,
                response.getPage() != null ? response.getPage() : page,
                response.getTotalPages() != null
                        ? Math.min(response.getTotalPages(), TMDB_MAX_PAGE)
                        : 0,
                response.getTotalResults() != null ? response.getTotalResults() : 0);
    }

    @Override
    public MoviePage searchMovies(String keyword, int page) {
        TmdbMovieListResponse response = tmdbRestClient.searchMovies(keyword, page);

        if (response == null || response.getResults() == null) {
            return new MoviePage(
                    Collections.emptyList(),
                    page,
                    0,
                    0);
        }

        List<Movie> movies = response.getResults().stream()
                .map(this::toMovie)
                .toList();

        return new MoviePage(
                movies,
                response.getPage() != null ? response.getPage() : page,
                response.getTotalPages() != null
                        ? Math.min(response.getTotalPages(), TMDB_MAX_PAGE)
                        : 0,
                response.getTotalResults() != null ? response.getTotalResults() : 0);
    }

    @Override
    public Movie getMovie(String tmdbMovieId) {
        Movie movie = toMovie(tmdbRestClient.getMovie(tmdbMovieId));

        if (movie == null) {
            return null;
        }

        boolean missingPoster = movie.getPosterPath() == null || movie.getPosterPath().isBlank();

        boolean missingBackdrop = movie.getBackdropPath() == null || movie.getBackdropPath().isBlank();

        if (!missingPoster && !missingBackdrop) {
            return movie;
        }

        long secondaryImageRequestStartNanos = System.nanoTime();
        TmdbMovieImagesResponse images = tmdbRestClient.getMovieImages(tmdbMovieId);
        long secondaryImageRequestMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - secondaryImageRequestStartNanos);
        log.info("TMDB secondary image lookup durationMs={} movieIdPresent={} missingPoster={} missingBackdrop={}",
                secondaryImageRequestMs,
                tmdbMovieId != null && !tmdbMovieId.isBlank(),
                missingPoster,
                missingBackdrop);

        if (images == null) {
            return movie;
        }

        if (missingPoster
                && images.getPosters() != null
                && !images.getPosters().isEmpty()) {

            String posterPath = images.getPosters().stream()
                    .map(TmdbImageResponse::getFilePath)
                    .filter(path -> path != null && !path.isBlank())
                    .findFirst()
                    .orElse(null);

            movie.setPosterPath(posterPath);
        }

        if (missingBackdrop
                && images.getBackdrops() != null
                && !images.getBackdrops().isEmpty()) {

            String backdropPath = images.getBackdrops().stream()
                    .map(TmdbImageResponse::getFilePath)
                    .filter(path -> path != null && !path.isBlank())
                    .findFirst()
                    .orElse(null);

            movie.setBackdropPath(backdropPath);
        }

        return movie;
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
                            .toList());
        }

        movie.setPosterPath(response.getPosterPath());
        movie.setBackdropPath(response.getBackdropPath());

        if (response.getReleaseDate() != null
                && !response.getReleaseDate().isBlank()) {
            movie.setReleaseDate(
                    LocalDate.parse(response.getReleaseDate()));
        }

        return movie;
    }

    @Override
    public List<Video> getMovieVideos(String tmdbMovieId) {

        TmdbVideoListResponse response = tmdbRestClient.getMovieVideos(tmdbMovieId);

        if (response == null || response.getResults() == null) {
            return Collections.emptyList();
        }

        return response.getResults()
                .stream()
                .map(this::toVideo)
                .toList();
    }

    private Video toVideo(TmdbVideoResponse response) {

        if (response == null) {
            return null;
        }

        Video video = new Video();

        video.setKey(response.getKey());
        video.setName(response.getName());
        video.setSite(response.getSite());
        video.setType(response.getType());
        video.setOfficial(response.isOfficial());

        return video;
    }

    @Override
    public List<Movie> discoverMoviesByGenres(List<Integer> genreIds) {

        TmdbMovieListResponse response = tmdbRestClient.discoverMoviesByGenres(genreIds);

        if (response == null || response.getResults() == null) {
            return Collections.emptyList();
        }

        return response.getResults()
                .stream()
                .map(this::toMovie)
                .toList();
    }

    @Override
    public MoviePage discoverMovies(
            List<Integer> genreIds,
            Integer startYear,
            Integer endYear,
            Double minRating,
            String sortBy,
            int page) {

        TmdbMovieListResponse response = tmdbRestClient.discoverMovies(
                genreIds,
                startYear,
                endYear,
                minRating,
                sortBy,
                page);

        if (response == null || response.getResults() == null) {
            return new MoviePage(
                    Collections.emptyList(),
                    page,
                    0,
                    0);
        }

        List<Movie> movies = response.getResults()
                .stream()
                .map(this::toMovie)
                .toList();

        return new MoviePage(
                movies,
                response.getPage() != null ? response.getPage() : page,
                response.getTotalPages() != null
                        ? Math.min(response.getTotalPages(), TMDB_MAX_PAGE)
                        : 0,
                response.getTotalResults() != null ? response.getTotalResults() : 0);
    }

}