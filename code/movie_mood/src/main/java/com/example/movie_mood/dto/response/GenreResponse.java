package com.example.movie_mood.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Genre information")
public class GenreResponse {

    @Schema(example = "35")
    private String genreId;

    @Schema(example = "Comedy")
    private String genreName;

    public GenreResponse() {
    }

    public GenreResponse(String genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}