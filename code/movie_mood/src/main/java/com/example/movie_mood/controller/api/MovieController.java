package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.service.MovieService;
import org.springframework.web.bind.annotation.*;
import com.example.movie_mood.facade.MovieDetailFacade;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;
    private final MovieDetailFacade movieDetailFacade;
    public MovieController(
        MovieService movieService,
        MovieDetailFacade movieDetailFacade) {

    this.movieService = movieService;
    this.movieDetailFacade = movieDetailFacade;
}

    @GetMapping
    public List<Movie> browseMovies() {
        return movieService.browseMovies();
    }

    @GetMapping("/search")
    public List<Movie> searchMovies(
            @RequestParam String keyword) {
        return movieService.searchMovies(keyword);
    }

    @GetMapping("/{tmdbMovieId}")
    public Movie getMovieDetails(
            @PathVariable Long tmdbMovieId) {
       return movieDetailFacade.getMovieDetails(tmdbMovieId);
    }
}
