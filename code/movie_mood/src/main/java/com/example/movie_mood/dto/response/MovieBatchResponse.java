package com.example.movie_mood.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieBatchResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("name")
    private String name;

    public MovieBatchResponse() {
    }

    public MovieBatchResponse(String id, String posterPath, String name) {
        this.id = id;
        this.posterPath = posterPath;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
