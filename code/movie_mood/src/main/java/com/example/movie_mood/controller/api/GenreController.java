package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.entity.Genre;
import com.example.movie_mood.dto.GenreRequest;
import com.example.movie_mood.dto.response.GenreResponse;
import com.example.movie_mood.dto.response.ErrorResponse;
import com.example.movie_mood.mapper.GenreMapper;
import com.example.movie_mood.service.GenreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
@Validated
@Tag(name = "Genres", description = "Genre management APIs")
public class GenreController {

    private final GenreService genreService;
    private final GenreMapper genreMapper;

    public GenreController(
            GenreService genreService,
            GenreMapper genreMapper) {
        this.genreService = genreService;
        this.genreMapper = genreMapper;
    }

    @Operation(summary = "Create a genre")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Genre created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid genre data",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<GenreResponse> createGenre(
            @Valid @RequestBody GenreRequest request) {

        Genre genre = genreMapper.toEntity(request);
        Genre createdGenre = genreService.createGenre(genre);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(genreMapper.toResponse(createdGenre));
    }

    @Operation(
            summary = "Get all genres",
            description = "Returns genres with pagination and sorting"
    )
    @GetMapping
    public Page<GenreResponse> getAllGenres(
        @ParameterObject Pageable pageable) {

    return genreService.getAllGenres(pageable)
            .map(genreMapper::toResponse);
}

    @Operation(summary = "Get genre by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Genre found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Genre not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{genreId}")
    public GenreResponse getGenreById(
            @PathVariable
            @Positive(message = "genreId must be greater than 0")
            Integer genreId) {

        return genreMapper.toResponse(
                genreService.getGenreById(genreId)
        );
    }

    @Operation(summary = "Update a genre")
    @PutMapping("/{genreId}")
    public GenreResponse updateGenre(
            @PathVariable
            @Positive(message = "genreId must be greater than 0")
            Integer genreId,
            @Valid @RequestBody GenreRequest request) {

        Genre genre = genreMapper.toEntity(request);

        return genreMapper.toResponse(
                genreService.updateGenre(genreId, genre)
        );
    }

    @Operation(summary = "Delete a genre")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Genre deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Genre not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> deleteGenre(
            @PathVariable
            @Positive(message = "genreId must be greater than 0")
            Integer genreId) {

        genreService.deleteGenre(genreId);

        return ResponseEntity.noContent().build();
    }
}