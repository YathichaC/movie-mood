package com.example.movie_mood.controller.api;

import com.example.movie_mood.dto.response.MovieResponse;
import com.example.movie_mood.facade.MovieDetailFacade;
import com.example.movie_mood.mapper.MovieMapper;
import com.example.movie_mood.service.MovieService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieController {

    private final MovieService movieService;
    private final MovieDetailFacade movieDetailFacade;
    private final MovieMapper movieMapper;

    public MovieController(
            MovieService movieService,
            MovieDetailFacade movieDetailFacade,
            MovieMapper movieMapper) {

        this.movieService = movieService;
        this.movieDetailFacade = movieDetailFacade;
        this.movieMapper = movieMapper;
    }

    @GetMapping
    public List<MovieResponse> browseMovies() {

        return movieMapper.toResponseList(
                movieService.browseMovies()
        );
    }

   @GetMapping("/search")
    public List<MovieResponse> searchMovies(
        @RequestParam @NotBlank String keyword) {

    return movieMapper.toResponseList(
            movieService.searchMovies(keyword)
    );
    }

    @GetMapping("/{tmdbMovieId}")
public MovieResponse getMovieDetails(
        @PathVariable @Positive Long tmdbMovieId) {

    return movieMapper.toResponse(
            movieDetailFacade.getMovieDetails(tmdbMovieId)
     );
    }

}