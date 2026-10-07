package com.example.movie_mood.service;

import com.example.movie_mood.dto.MovielistRequest;
import com.example.movie_mood.dto.MovielistResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;

import java.util.List;
import java.util.UUID;

public interface PlaylistService {

    List<PlaylistResponse> getUserPlaylists(
            UUID userId);

    PlaylistResponse getPlaylistDetail(
            UUID playlistId,
            UUID userId);

    PlaylistResponse createPlaylist(
            UUID userId,
            PlaylistRequest request);

    PlaylistResponse updatePlaylist(
            UUID playlistId,
            UUID userId,
            PlaylistRequest request);

    void deletePlaylist(
            UUID playlistId,
            UUID userId);

    MovielistResponse addMovieToPlaylist(
            UUID playlistId,
            UUID userId,
            MovielistRequest request);

    void removeMovieFromPlaylist(
            UUID playlistId,
            String tmdbMovieId,
            UUID userId);
}