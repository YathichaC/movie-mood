package com.example.movie_mood.controller;

import com.example.movie_mood.dto.MovielistRequest;
import com.example.movie_mood.dto.MovielistResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistRestController {

    private final PlaylistService playlistService;

    public PlaylistRestController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<?> getPlaylists(HttpSession session) {
        UUID userId = getCurrentUserId(session);

        if (userId == null) {
            return unauthorized();
        }
        List<PlaylistResponse> playlists = playlistService.getUserPlaylists(userId);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaylistDetail(@PathVariable("id") UUID id, HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        PlaylistResponse playlist = playlistService.getPlaylistDetail(id, userId);
        return ResponseEntity.ok(playlist);
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@RequestBody PlaylistRequest request, HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        PlaylistResponse created = playlistService.createPlaylist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(@PathVariable("id") UUID id, HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        playlistService.deletePlaylist(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/movies")
    public ResponseEntity<?> addMovieToPlaylist(
            @PathVariable("id") UUID id,
            @RequestBody MovielistRequest request, HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        MovielistResponse item = playlistService.addMovieToPlaylist(id, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/{id}/movies/{tmdbMovieId}")
    public ResponseEntity<?> removeMovieFromPlaylist(
            @PathVariable("id") UUID id,
            @PathVariable("tmdbMovieId") String tmdbMovieId, HttpSession session) {
        UUID userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        playlistService.removeMovieFromPlaylist(id, tmdbMovieId, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId(HttpSession session) {
        return (UUID) session.getAttribute("USER_ID");
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Please login first"));
    }
}