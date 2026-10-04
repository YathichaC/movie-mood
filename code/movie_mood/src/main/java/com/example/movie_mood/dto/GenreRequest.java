package com.example.movie_mood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GenreRequest {

    @NotNull(message = "genreId is required")
    @Positive(message = "genreId must be greater than 0")
    private Integer genreId;

    @NotBlank(message = "genreName is required")
    private String genreName;

    public GenreRequest() {
    }

    public GenreRequest(Integer genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}