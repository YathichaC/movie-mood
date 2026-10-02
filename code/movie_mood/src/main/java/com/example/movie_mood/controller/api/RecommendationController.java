package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<Movie> getRecommendations(
            @RequestParam Mood mood,
            @RequestParam(
                    required = false,
                    defaultValue = ""
            ) List<Integer> dislikedGenreIds) {

        return recommendationService.getRecommendations(
                mood,
                dislikedGenreIds
        );
    }
}