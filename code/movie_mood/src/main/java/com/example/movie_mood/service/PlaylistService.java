package com.example.movie_mood.service;

import com.example.movie_mood.dto.MovielistRequest;
import com.example.movie_mood.dto.MovielistResponse;
import com.example.movie_mood.dto.PlaylistMovieBatchRequest;
import com.example.movie_mood.dto.PlaylistPickerResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.dto.PlaylistSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface PlaylistService {

        List<PlaylistSummaryResponse> getUserPlaylists(
                        UUID userId);

        List<PlaylistPickerResponse> getUserPlaylistsForPicker(
                        UUID userId,
                        String tmdbMovieId);

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

        void updateMoviePlaylists(
                        UUID userId,
                        PlaylistMovieBatchRequest request);

        void removeMovieFromPlaylist(
                        UUID playlistId,
                        String tmdbMovieId,
                        UUID userId);

        String deletePlaylistImage(
                        UUID playlistId,
                        UUID userId);
}