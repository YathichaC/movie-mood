package com.example.movie_mood.controller;

import com.example.movie_mood.dto.MovielistRequest;
import com.example.movie_mood.dto.MovielistResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.UUID;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/playlists")
@SecurityRequirement(name = "bearerAuth")
public class PlaylistRestController {

    private final PlaylistService playlistService;

    public PlaylistRestController(
            PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<?> getPlaylists(
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);

        List<PlaylistResponse> playlists = playlistService.getUserPlaylists(userId);

        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaylistDetail(
            @PathVariable("id") UUID id,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);

        PlaylistResponse playlist = playlistService.getPlaylistDetail(
                id,
                userId);

        return ResponseEntity.ok(playlist);
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(
            Authentication authentication,
            @Valid @RequestBody PlaylistRequest request) {

        UUID userId = getCurrentUserId(authentication);

        PlaylistResponse created = playlistService.createPlaylist(
                userId,
                request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlaylist(
            @PathVariable("id") UUID id,
            Authentication authentication,
            @Valid @RequestBody PlaylistRequest request) {

        UUID userId = getCurrentUserId(authentication);

        PlaylistResponse updated = playlistService.updatePlaylist(
                id,
                userId,
                request);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(
            @PathVariable("id") UUID id,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);

        playlistService.deletePlaylist(
                id,
                userId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/{id}/movies")
    public ResponseEntity<?> addMovieToPlaylist(
            @PathVariable("id") UUID id,
            @RequestBody MovielistRequest request,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);

        MovielistResponse item = playlistService.addMovieToPlaylist(
                id,
                userId,
                request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(item);
    }

    @DeleteMapping("/{id}/movies/{tmdbMovieId}")
    public ResponseEntity<?> removeMovieFromPlaylist(
            @PathVariable("id") UUID id,
            @PathVariable("tmdbMovieId") String tmdbMovieId,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);

        playlistService.removeMovieFromPlaylist(
                id,
                tmdbMovieId,
                userId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private UUID getCurrentUserId(
            Authentication authentication) {

        return UUID.fromString(
                authentication.getName());
    }
}