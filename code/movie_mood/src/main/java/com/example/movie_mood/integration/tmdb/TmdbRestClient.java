package com.example.movie_mood.integration.tmdb;

import com.example.movie_mood.integration.tmdb.dto.TmdbMovieListResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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

    public TmdbMovieResponse getMovie(Long tmdbMovieId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("language", "en-US")
                        .build(tmdbMovieId))
                .retrieve()
                .body(TmdbMovieResponse.class);
    }
}