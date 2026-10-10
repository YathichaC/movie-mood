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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

@Service
public class MovieServiceImpl implements MovieService {

    private static final int MAX_BATCH_MOVIE_REQUEST_SIZE = 50;
    private static final int SEARCH_FILTER_MAX_TMDB_PAGES = 10;
    private static final int SEARCH_FILTER_PAGE_SIZE = 20;
    private static final List<String> ALLOWED_DISCOVER_SORT_VALUES = List.of(
            "rating_desc",
            "rating_asc",
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
    public MoviePage searchMoviesWithFilters(
            String keyword,
            List<Integer> genreIds,
            Integer startYear,
            Integer endYear,
            Double minRating,
            String sortBy,
            int page) {

        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("keyword must not be blank");
        }

        if (startYear != null && endYear != null && startYear > endYear) {
            throw new IllegalArgumentException(
                    "startYear must not be greater than endYear");
        }

        if (sortBy != null && !sortBy.isBlank()
                && !ALLOWED_DISCOVER_SORT_VALUES.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy value");
        }

        MoviePage firstPage = movieProvider.searchMovies(keyword.trim(), 1);

        int pagesToFetch = Math.min(
                firstPage.getTotalPages(),
                SEARCH_FILTER_MAX_TMDB_PAGES);

        List<Movie> collectedMovies = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();

        for (int tmdbPage = 1; tmdbPage <= pagesToFetch; tmdbPage++) {

            MoviePage result = tmdbPage == 1
                    ? firstPage
                    : movieProvider.searchMovies(keyword.trim(), tmdbPage);

            for (Movie movie : result.getMovies()) {

                if (movie == null || movie.getTmdbMovieId() == null) {
                    continue;
                }

                String id = String.valueOf(movie.getTmdbMovieId());

                if (seenIds.add(id)) {
                    collectedMovies.add(movie);
                }
            }
        }

        List<Movie> filteredMovies = new ArrayList<>(
                collectedMovies.stream()
                        .filter(movie -> {
                            if (genreIds == null || genreIds.isEmpty()) {
                                return true;
                            }

                            if (movie.getGenreIds() == null) {
                                return false;
                            }

                            return movie.getGenreIds().stream()
                                    .anyMatch(id -> genreIds.stream()
                                            .anyMatch(selected -> String.valueOf(id)
                                                    .equals(String.valueOf(selected))));
                        })
                        .filter(movie -> minRating == null
                                || (movie.getRating() != null
                                        && movie.getRating() >= minRating))
                        .filter(movie -> {
                            if (startYear == null && endYear == null) {
                                return true;
                            }

                            if (movie.getReleaseDate() == null) {
                                return false;
                            }

                            int year = movie.getReleaseDate().getYear();

                            return (startYear == null || year >= startYear)
                                    && (endYear == null || year <= endYear);
                        })
                        .toList());

        if (sortBy != null && !sortBy.isBlank()) {

            Comparator<Movie> comparator = switch (sortBy) {
                case "rating_desc" -> Comparator.comparing(
                        Movie::getRating,
                        Comparator.nullsLast(Comparator.reverseOrder()));

                case "rating_asc" -> Comparator.comparing(
                        Movie::getRating,
                        Comparator.nullsLast(Comparator.naturalOrder()));

                case "release_desc" -> Comparator.comparing(
                        Movie::getReleaseDate,
                        Comparator.nullsLast(Comparator.reverseOrder()));

                case "release_asc" -> Comparator.comparing(
                        Movie::getReleaseDate,
                        Comparator.nullsLast(Comparator.naturalOrder()));

                case "alphabet_asc" -> Comparator.comparing(
                        movie -> movie.getTitle() == null
                                ? ""
                                : movie.getTitle().toLowerCase(Locale.ROOT));

                default -> throw new IllegalArgumentException(
                        "Invalid sortBy value");
            };

            filteredMovies.sort(comparator);
        }

        int totalElements = filteredMovies.size();

        int totalPages = (totalElements + SEARCH_FILTER_PAGE_SIZE - 1)
                / SEARCH_FILTER_PAGE_SIZE;

        int fromIndex = (page - 1) * SEARCH_FILTER_PAGE_SIZE;

        if (fromIndex >= totalElements) {
            return new MoviePage(
                    List.of(),
                    page,
                    totalPages,
                    totalElements);
        }

        int toIndex = Math.min(
                fromIndex + SEARCH_FILTER_PAGE_SIZE,
                totalElements);

        return new MoviePage(
                new ArrayList<>(filteredMovies.subList(fromIndex, toIndex)),
                page,
                totalPages,
                totalElements);
    }

    @Override
    public List<Movie> getMovieBatch(List<String> tmdbMovieIds) {
        if (tmdbMovieIds == null || tmdbMovieIds.isEmpty()) {
            return List.of();
        }

        if (tmdbMovieIds.size() > MAX_BATCH_MOVIE_REQUEST_SIZE) {
            throw new IllegalArgumentException(
                    "Movie batch request exceeds the maximum size of "
                            + MAX_BATCH_MOVIE_REQUEST_SIZE
                            + " movies");
        }

        List<Movie> movies = new java.util.ArrayList<>();

        for (String tmdbMovieId : tmdbMovieIds) {
            if (tmdbMovieId == null) {
                continue;
            }

            String normalizedMovieId = tmdbMovieId.trim();
            if (normalizedMovieId.isBlank() || !normalizedMovieId.matches("\\d+")) {
                continue;
            }

            Movie movie = movieProvider.getMovie(normalizedMovieId);
            if (movie != null) {
                movies.add(movie);
            }
        }

        return movies;
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
    public Movie getMovieSummaryForHistory(String tmdbMovieId) {
        Movie movie = movieProvider.getMovie(tmdbMovieId);
        if (movie == null) {
            return null;
        }

        Movie summary = new Movie();
        summary.setTmdbMovieId(movie.getTmdbMovieId());
        summary.setTitle(movie.getTitle());
        summary.setPosterPath(movie.getPosterPath());
        summary.setReleaseDate(movie.getReleaseDate());
        summary.setRating(movie.getRating());
        return summary;
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
            List<Integer> genreIds,
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
                genreIds,
                startYear,
                endYear,
                minRating,
                sortBy,
                page);
    }

}
