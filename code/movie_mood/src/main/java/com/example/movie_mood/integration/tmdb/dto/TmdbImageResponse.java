package com.example.movie_mood.integration.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TmdbImageResponse {

    @JsonProperty("file_path")
    private String filePath;

    private Integer width;
    private Integer height;

    @JsonProperty("vote_average")
    private Double voteAverage;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }
}