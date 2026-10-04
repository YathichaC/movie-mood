package com.example.movie_mood.controller;

import com.example.movie_mood.dto.PlaylistItemRequest;
import com.example.movie_mood.dto.PlaylistItemResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistRestController {

    private final PlaylistService playlistService;

    private static final Long DEFAULT_USER_ID = 1L;

    public PlaylistRestController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<List<PlaylistResponse>> getPlaylists() {
        List<PlaylistResponse> playlists = playlistService.getUserPlaylists(DEFAULT_USER_ID);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponse> getPlaylistDetail(@PathVariable("id") Long id) {
        PlaylistResponse playlist = playlistService.getPlaylistDetail(id, DEFAULT_USER_ID);
        return ResponseEntity.ok(playlist);
    }

    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(@RequestBody PlaylistRequest request) {
        PlaylistResponse created = playlistService.createPlaylist(DEFAULT_USER_ID, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable("id") Long id) {
        playlistService.deletePlaylist(id, DEFAULT_USER_ID);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/movies")
    public ResponseEntity<PlaylistItemResponse> addMovieToPlaylist(
            @PathVariable("id") Long id,
            @RequestBody PlaylistItemRequest request) {
        PlaylistItemResponse item = playlistService.addMovieToPlaylist(id, DEFAULT_USER_ID, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/{id}/movies/{tmdbMovieId}")
    public ResponseEntity<Void> removeMovieFromPlaylist(
            @PathVariable("id") Long id,
            @PathVariable("tmdbMovieId") Long tmdbMovieId) {
        playlistService.removeMovieFromPlaylist(id, tmdbMovieId, DEFAULT_USER_ID);
        return ResponseEntity.noContent().build();
    }
}