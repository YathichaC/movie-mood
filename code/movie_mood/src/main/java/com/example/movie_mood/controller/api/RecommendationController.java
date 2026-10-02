package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.dto.response.MovieResponse;
import com.example.movie_mood.mapper.MovieMapper;
import com.example.movie_mood.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final MovieMapper movieMapper;

    public RecommendationController(
        RecommendationService recommendationService,
        MovieMapper movieMapper) {

    this.recommendationService = recommendationService;
    this.movieMapper = movieMapper;
}

   @GetMapping
    public List<MovieResponse> getRecommendations(
        @RequestParam Mood mood,
        @RequestParam(
                required = false,
                defaultValue = ""
        ) List<Integer> dislikedGenreIds) {

    return movieMapper.toResponseList(
            recommendationService.getRecommendations(
                    mood,
                    dislikedGenreIds
            )
    );
}
}