package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.entity.Playlist;
import com.example.movie_mood.domain.entity.Movielist;
import com.example.movie_mood.dto.MovielistRequest;
import com.example.movie_mood.dto.MovielistResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.repository.MovielistRepository;
import com.example.movie_mood.repository.PlaylistRepository;
import com.example.movie_mood.service.PlaylistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.movie_mood.domain.entity.PlaylistDetail;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final MovielistRepository movielistRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository,
            MovielistRepository movielistRepository) {
        this.playlistRepository = playlistRepository;
        this.movielistRepository = movielistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaylistResponse> getUserPlaylists(UUID userId) {
        return playlistRepository.findByUserId(userId)
                .stream()
                .map(PlaylistResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistResponse getPlaylistDetail(UUID playlistId, UUID userId) {
        Playlist playlist = playlistRepository.findByPlaylistIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found or access denied"));
        return new PlaylistResponse(playlist);
    }

    @Override
    public PlaylistResponse createPlaylist(UUID userId, PlaylistRequest request) {
        Playlist playlist = new Playlist(userId, request.getPlaylistName());

        PlaylistDetail detail = new PlaylistDetail(
                playlist,
                request.getDetail(),
                request.getCoverImagePath());

        playlist.setDetail(detail);

        Playlist saved = playlistRepository.save(playlist);
        return new PlaylistResponse(saved);
    }

    @Override
    public void deletePlaylist(UUID playlistId, UUID userId) {
        Playlist playlist = playlistRepository.findByPlaylistIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found or access denied"));
        playlistRepository.delete(playlist);
    }

    @Override
    public MovielistResponse addMovieToPlaylist(UUID playlistId, UUID userId, MovielistRequest request) {
        Playlist playlist = playlistRepository.findByPlaylistIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found or access denied"));

        if (movielistRepository.existsByPlaylist_PlaylistIdAndTmdbMovieId(playlistId, request.getTmdbMovieId())) {
            throw new IllegalStateException("Movie is already in this playlist");
        }

        Movielist item = new Movielist(
                playlist,
                request.getTmdbMovieId());

        playlist.addItem(item);
        Movielist savedItem = movielistRepository.save(item);
        return new MovielistResponse(savedItem);
    }

    @Override
    public void removeMovieFromPlaylist(UUID playlistId, String tmdbMovieId, UUID userId) {
        playlistRepository.findByPlaylistIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found or access denied"));

        movielistRepository.deleteByPlaylist_PlaylistIdAndTmdbMovieId(playlistId, tmdbMovieId);
    }
}