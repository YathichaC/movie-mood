package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.dto.response.MovieResponse;
import com.example.movie_mood.mapper.MovieMapper;
import com.example.movie_mood.service.RecommendationService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import com.example.movie_mood.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;



@RestController
@RequestMapping("/api/recommendations")
@Tag(
        name = "Recommendations",
        description = "Get movie recommendations based on mood and disliked genres"
)
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final MovieMapper movieMapper;

    public RecommendationController(
        RecommendationService recommendationService,
        MovieMapper movieMapper) {

    this.recommendationService = recommendationService;
    this.movieMapper = movieMapper;
}
    @Operation(
        summary = "Get movie recommendations by mood",
        description = "Returns movie recommendations based on the selected mood "
                + "while excluding movies that contain disliked genres"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Recommendations retrieved successfully"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid or missing mood or invalid genre IDs",
                content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
                )
        )
    })
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