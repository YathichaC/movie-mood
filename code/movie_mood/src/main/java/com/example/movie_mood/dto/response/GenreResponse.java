package com.example.movie_mood.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Genre information")
public class GenreResponse {

    @Schema(example = "35")
    private Integer genreId;

    @Schema(example = "Comedy")
    private String genreName;

    public GenreResponse() {
    }

    public GenreResponse(Integer genreId, String genreName) {
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