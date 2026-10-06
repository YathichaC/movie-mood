package com.example.movie_mood.service;

import com.example.movie_mood.dto.PlaylistItemRequest;
import com.example.movie_mood.dto.PlaylistItemResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;

import java.util.List;

public interface PlaylistService {
    List<PlaylistResponse> getUserPlaylists(Long userId);
    PlaylistResponse getPlaylistDetail(Long playlistId, Long userId);
    PlaylistResponse createPlaylist(Long userId, PlaylistRequest request);
    void deletePlaylist(Long playlistId, Long userId);
    PlaylistItemResponse addMovieToPlaylist(Long playlistId, Long userId, PlaylistItemRequest request);
    void removeMovieFromPlaylist(Long playlistId, Long tmdbMovieId, Long userId);
}