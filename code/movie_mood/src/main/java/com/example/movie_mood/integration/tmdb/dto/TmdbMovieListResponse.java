package com.example.movie_mood.integration.tmdb.dto;


import java.util.List;

public class TmdbMovieListResponse {

    private Integer page;
    private List<TmdbMovieResponse> results;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<TmdbMovieResponse> getResults() {
        return results;
    }

    public void setResults(List<TmdbMovieResponse> results) {
        this.results = results;
    }
}