package com.example.movie_mood.controller;

import com.example.movie_mood.dto.PlaylistItemRequest;
import com.example.movie_mood.dto.PlaylistItemResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

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
        Integer userId = getCurrentUserId(session);

        if (userId == null) {
            return unauthorized();
        }
        List<PlaylistResponse> playlists = playlistService.getUserPlaylists(userId.longValue());
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaylistDetail(@PathVariable("id") Long id, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        PlaylistResponse playlist = playlistService.getPlaylistDetail(id, userId.longValue());
        return ResponseEntity.ok(playlist);
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@RequestBody PlaylistRequest request, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        PlaylistResponse created = playlistService.createPlaylist(userId.longValue(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(@PathVariable("id") Long id, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        playlistService.deletePlaylist(id, userId.longValue());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/movies")
    public ResponseEntity<?> addMovieToPlaylist(
            @PathVariable("id") Long id,
            @RequestBody PlaylistItemRequest request, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        PlaylistItemResponse item = playlistService.addMovieToPlaylist(id, userId.longValue(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/{id}/movies/{tmdbMovieId}")
    public ResponseEntity<?> removeMovieFromPlaylist(
            @PathVariable("id") Long id,
            @PathVariable("tmdbMovieId") Long tmdbMovieId, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            return unauthorized();
        }
        playlistService.removeMovieFromPlaylist(id, tmdbMovieId, userId.longValue());
        return ResponseEntity.noContent().build();
    }

    private Integer getCurrentUserId(HttpSession session) {
        return (Integer) session.getAttribute("USER_ID");
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Please login first"));
    }
}