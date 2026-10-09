package com.example.movie_mood.integration.tmdb.dto;

import java.util.List;

public class TmdbVideoListResponse {

    private String id;
    private List<TmdbVideoResponse> results;

    public TmdbVideoListResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TmdbVideoResponse> getResults() {
        return results;
    }

    public void setResults(List<TmdbVideoResponse> results) {
        this.results = results;
    }
}