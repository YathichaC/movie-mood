package com.example.movie_mood.dto;

public class TmdbGenreResponse {

    private String id;
    private String name;

    public TmdbGenreResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
