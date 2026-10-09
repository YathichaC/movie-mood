package com.example.movie_mood.integration.tmdb;

import com.example.movie_mood.integration.tmdb.dto.TmdbMovieImagesResponse;
import com.example.movie_mood.exception.MovieNotFoundException;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieListResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.example.movie_mood.integration.tmdb.dto.TmdbVideoListResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class TmdbRestClient {

        private static final Logger log = LoggerFactory.getLogger(TmdbRestClient.class);

        private final RestClient restClient;

        public TmdbRestClient(
                        @Value("${tmdb.api.base-url}") String baseUrl,
                        @Value("${tmdb.api.token}") String token,
                        @Value("${tmdb.http.connect-timeout-ms:5000}") int connectTimeoutMs,
                        @Value("${tmdb.http.read-timeout-ms:8000}") int readTimeoutMs) {

                SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                requestFactory.setConnectTimeout(connectTimeoutMs);
                requestFactory.setReadTimeout(readTimeoutMs);

                this.restClient = RestClient.builder()
                                .baseUrl(baseUrl)
                                .requestFactory(requestFactory)
                                .defaultHeader("Authorization", "Bearer " + token)
                                .defaultHeader("Accept", "application/json")
                                .build();
        }

        public TmdbMovieListResponse getPopularMovies(int page) {
                return executeTimedRequest("getPopularMovies", () -> restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/movie/popular")
                                                .queryParam("language", "en-US")
                                                .queryParam("page", page)
                                                .build())
                                .retrieve()
                                .body(TmdbMovieListResponse.class));
        }

        public TmdbMovieListResponse searchMovies(String keyword, int page) {
                return executeTimedRequest("searchMovies", () -> restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/search/movie")
                                                .queryParam("query", keyword)
                                                .queryParam("include_adult", false)
                                                .queryParam("language", "en-US")
                                                .queryParam("page", page)
                                                .build())
                                .retrieve()
                                .body(TmdbMovieListResponse.class));
        }

        public TmdbMovieResponse getMovie(String tmdbMovieId) {
                return executeTimedRequest("getMovie", () -> restClient.get()
                                .uri("/movie/{id}", tmdbMovieId)
                                .retrieve()
                                .onStatus(
                                                status -> status.value() == 404,
                                                (request, response) -> {
                                                        throw new MovieNotFoundException(tmdbMovieId);
                                                })
                                .body(TmdbMovieResponse.class));
        }

        public TmdbVideoListResponse getMovieVideos(String tmdbMovieId) {
                return executeTimedRequest("getMovieVideos", () -> restClient.get()
                                .uri("/movie/{id}/videos?language=en-US", tmdbMovieId)
                                .retrieve()
                                .body(TmdbVideoListResponse.class));
        }

        public TmdbMovieImagesResponse getMovieImages(String tmdbMovieId) {
                return executeTimedRequest("getMovieImages", () -> restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/movie/{id}/images")
                                                .queryParam("include_image_language", "en,null")
                                                .build(tmdbMovieId))
                                .retrieve()
                                .onStatus(
                                                status -> status.value() == 404,
                                                (request, response) -> {
                                                        throw new MovieNotFoundException(tmdbMovieId);
                                                })
                                .body(TmdbMovieImagesResponse.class));
        }

        private <T> T executeTimedRequest(String operation, Supplier<T> requestSupplier) {
                long startNanos = System.nanoTime();
                try {
                        return requestSupplier.get();
                } finally {
                        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
                        log.info("TMDB HTTP request operation={} durationMs={}", operation, durationMs);
                }
        }

        public TmdbMovieListResponse discoverMoviesByGenres(List<Integer> genreIds) {

                String genres = genreIds.stream()
                                .map(String::valueOf)
                                .collect(java.util.stream.Collectors.joining("|"));

                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/discover/movie")
                                                .queryParam("with_genres", genres)
                                                .queryParam("include_adult", false)
                                                .queryParam("language", "en-US")
                                                .queryParam("page", 1)
                                                .build())
                                .retrieve()
                                .body(TmdbMovieListResponse.class);
        }

        public TmdbMovieListResponse discoverMovies(
                        List<Integer> genreIds,
                        Integer startYear,
                        Integer endYear,
                        Double minRating,
                        String sortBy,
                        int page) {

                String tmdbSortBy;

                if (sortBy == null || sortBy.isBlank()) {
                        tmdbSortBy = "popularity.desc";
                } else {
                        tmdbSortBy = switch (sortBy) {
                                case "alphabet_asc" -> "original_title.asc";
                                case "release_desc" -> "primary_release_date.desc";
                                case "release_asc" -> "primary_release_date.asc";
                                case "rating_desc" -> "vote_average.desc";
                                case "rating_asc" -> "vote_average.asc";
                                default -> "popularity.desc";
                        };
                }

                return restClient.get()
                                .uri(uriBuilder -> {
                                        uriBuilder
                                                        .path("/discover/movie")
                                                        .queryParam("include_adult", false)
                                                        .queryParam("language", "en-US")
                                                        .queryParam("page", page)
                                                        .queryParam("sort_by", tmdbSortBy);

                                        if (genreIds != null && !genreIds.isEmpty()) {
                                                String genres = genreIds.stream()
                                                                .map(String::valueOf)
                                                                .collect(java.util.stream.Collectors.joining("|"));

                                                uriBuilder.queryParam("with_genres", genres);
                                        }

                                        if (startYear != null) {
                                                uriBuilder.queryParam(
                                                                "primary_release_date.gte",
                                                                startYear + "-01-01");
                                        }

                                        if (endYear != null) {
                                                uriBuilder.queryParam(
                                                                "primary_release_date.lte",
                                                                endYear + "-12-31");
                                        }

                                        if (minRating != null) {
                                                uriBuilder.queryParam(
                                                                "vote_average.gte",
                                                                minRating);
                                        }

                                        return uriBuilder.build();
                                })
                                .retrieve()
                                .body(TmdbMovieListResponse.class);
        }

}
