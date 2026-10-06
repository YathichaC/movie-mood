package com.example.movie_mood.integration.tmdb;

import com.example.movie_mood.exception.MovieNotFoundException;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieListResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.example.movie_mood.integration.tmdb.dto.TmdbVideoListResponse;
import java.util.List;

@Component
public class TmdbRestClient {

        private final RestClient restClient;

        public TmdbRestClient(
                        @Value("${tmdb.api.base-url}") String baseUrl,
                        @Value("${tmdb.api.token}") String token) {

                this.restClient = RestClient.builder()
                                .baseUrl(baseUrl)
                                .defaultHeader("Authorization", "Bearer " + token)
                                .defaultHeader("Accept", "application/json")
                                .build();
        }

        public TmdbMovieListResponse getPopularMovies() {
                return restClient.get()
                                .uri("/movie/popular?language=en-US&page=1")
                                .retrieve()
                                .body(TmdbMovieListResponse.class);
        }

        public TmdbMovieListResponse searchMovies(String keyword) {
                return restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/search/movie")
                                                .queryParam("query", keyword)
                                                .queryParam("include_adult", false)
                                                .queryParam("language", "en-US")
                                                .queryParam("page", 1)
                                                .build())
                                .retrieve()
                                .body(TmdbMovieListResponse.class);
        }

        public TmdbMovieResponse getMovie(String tmdbMovieId) {
                return restClient.get()
                                .uri("/movie/{id}", tmdbMovieId)
                                .retrieve()
                                .onStatus(
                                                status -> status.value() == 404,
                                                (request, response) -> {
                                                        throw new MovieNotFoundException(tmdbMovieId);
                                                })
                                .body(TmdbMovieResponse.class);
        }

        public TmdbVideoListResponse getMovieVideos(String tmdbMovieId) {
                return restClient.get()
                                .uri("/movie/{id}/videos?language=en-US", tmdbMovieId)
                                .retrieve()
                                .body(TmdbVideoListResponse.class);
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
}