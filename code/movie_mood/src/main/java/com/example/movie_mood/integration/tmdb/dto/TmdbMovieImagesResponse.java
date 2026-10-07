package com.example.movie_mood.integration.tmdb.dto;

import java.util.List;

public class TmdbMovieImagesResponse {

    private Integer id;
    private List<TmdbImageResponse> backdrops;
    private List<TmdbImageResponse> posters;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TmdbImageResponse> getBackdrops() {
        return backdrops;
    }

    public void setBackdrops(List<TmdbImageResponse> backdrops) {
        this.backdrops = backdrops;
    }

    public List<TmdbImageResponse> getPosters() {
        return posters;
    }

    public void setPosters(List<TmdbImageResponse> posters) {
        this.posters = posters;
    }
}