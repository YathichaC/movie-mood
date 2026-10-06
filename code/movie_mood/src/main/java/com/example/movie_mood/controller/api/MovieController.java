package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.model.Video;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.dto.response.MovieResponse;
import com.example.movie_mood.facade.MovieDetailFacade;
import com.example.movie_mood.mapper.MovieMapper;
import com.example.movie_mood.service.MovieService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import com.example.movie_mood.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@Validated
@Tag(name = "Movies", description = "Browse, search, and view movie details from TMDB")
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

        @Operation(summary = "Browse popular movies", description = "Returns a list of popular movies from TMDB")
        @ApiResponse(responseCode = "200", description = "Popular movies retrieved successfully")
        @GetMapping
        public List<MovieResponse> browseMovies() {

                return movieMapper.toResponseList(
                                movieService.browseMovies());
        }

        @Operation(summary = "Search movies", description = "Searches TMDB movies using the provided keyword")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Movies retrieved successfully"),
                        @ApiResponse(responseCode = "400", description = "Search keyword is blank or invalid", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/search")
        public List<MovieResponse> searchMovies(
                        @RequestParam @NotBlank String keyword) {

                return movieMapper.toResponseList(
                                movieService.searchMovies(keyword));
        }

        @Operation(summary = "Get movie details", description = "Returns details of a movie using its TMDB movie ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Movie retrieved successfully"),
                        @ApiResponse(responseCode = "400", description = "TMDB movie ID must be positive", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })

        @GetMapping("/{tmdbMovieId}")
        public MovieResponse getMovieDetails(
                        @PathVariable @NotBlank String tmdbMovieId) {

                return movieMapper.toResponse(
                                movieDetailFacade.getMovieDetails(tmdbMovieId));
        }

        @Operation(summary = "Get movie trailer", description = "Returns the official YouTube trailer of a movie from TMDB")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Trailer retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Trailer not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/{tmdbMovieId}/trailer")
        public ResponseEntity<Map<String, String>> getMovieTrailer(
                        @PathVariable @NotBlank String tmdbMovieId) {

                Video trailer = movieService.getMovieTrailer(tmdbMovieId);

                if (trailer == null) {
                        throw new IllegalArgumentException(
                                        "Trailer not found for movie: " + tmdbMovieId);
                }

                return ResponseEntity.ok(
                                Map.of("key", trailer.getKey()));
        }

        @Operation(summary = "Filter movies by genre", description = "Returns movies from TMDB filtered by a genre ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Movies filtered successfully"),
                        @ApiResponse(responseCode = "400", description = "Genre ID must be positive", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/filter")
        public List<MovieResponse> filterMoviesByGenre(
                        @RequestParam @Positive(message = "genreId must be greater than 0") Integer genreId) {

                return movieMapper.toResponseList(
                                movieService.filterMoviesByGenre(genreId));
        }

        @Operation(summary = "Filter movies by mood", description = "Returns movies from TMDB filtered by genres associated with the selected mood")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Movies filtered by mood successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid mood value", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/filter/mood")
        public List<MovieResponse> filterMoviesByMood(
                        @RequestParam Mood mood) {

                return movieMapper.toResponseList(
                                movieService.filterMoviesByMood(mood));
        }
}