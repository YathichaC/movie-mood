package com.example.movie_mood.integration.tmdb.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TmdbMovieResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializePosterAndBackdropPaths() throws Exception {
        String json = """
                {
                  "id": 550,
                  "title": "Fight Club",
                  "poster_path": "/poster.jpg",
                  "backdrop_path": "/backdrop.jpg"
                }
                """;

        TmdbMovieResponse response =
                objectMapper.readValue(json, TmdbMovieResponse.class);

        assertEquals(550L, response.getId());
        assertEquals("/poster.jpg", response.getPosterPath());
        assertEquals("/backdrop.jpg", response.getBackdropPath());
    }
}