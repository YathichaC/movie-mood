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

        assertEquals("550", response.getId());
        assertEquals("/poster.jpg", response.getPosterPath());
        assertEquals("/backdrop.jpg", response.getBackdropPath());
    }

    @Test
    void shouldDeserializeDetailGenres() throws Exception {
        String json = """
                {
                  "id": 550,
                  "title": "Fight Club",
                  "genres": [
                    {
                      "id": 18,
                      "name": "Drama"
                    },
                    {
                      "id": 53,
                      "name": "Thriller"
                    }
                  ]
                }
                """;

        TmdbMovieResponse response =
                objectMapper.readValue(json, TmdbMovieResponse.class);

        assertEquals(2, response.getGenres().size());

        assertEquals(18, response.getGenres().get(0).getId());
        assertEquals(
                "Drama",
                response.getGenres().get(0).getName()
        );

        assertEquals(53, response.getGenres().get(1).getId());
        assertEquals(
                "Thriller",
                response.getGenres().get(1).getName()
        );
    }
}