package com.example.movie_mood.domain.model;

import java.util.List;

public class MoviePage {

    private List<Movie> movies;
    private int page;
    private int totalPages;
    private int totalResults;

    public MoviePage() {
    }

    public MoviePage(
            List<Movie> movies,
            int page,
            int totalPages,
            int totalResults) {
        this.movies = movies;
        this.page = page;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
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

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}