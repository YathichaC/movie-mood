package com.example.movie_mood.dto.response;

import java.util.List;

public class MoviePageResponse {

    private List<MovieResponse> content;
    private int page;
    private int totalPages;
    private int totalElements;

    public MoviePageResponse() {
    }

    public MoviePageResponse(
            List<MovieResponse> content,
            int page,
            int totalPages,
            int totalElements) {

        this.content = content;
        this.page = page;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<MovieResponse> getContent() {
        return content;
    }

    public void setContent(List<MovieResponse> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}