package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.entity.Playlist;
import com.example.movie_mood.domain.entity.PlaylistItem;
import com.example.movie_mood.dto.PlaylistItemRequest;
import com.example.movie_mood.dto.PlaylistItemResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.repository.PlaylistItemRepository;
import com.example.movie_mood.repository.PlaylistRepository;
import com.example.movie_mood.service.PlaylistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository,
                               PlaylistItemRepository playlistItemRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistItemRepository = playlistItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaylistResponse> getUserPlaylists(Long userId) {
        return playlistRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(PlaylistResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistResponse getPlaylistDetail(Long playlistId, Long userId) {
        Playlist playlist = playlistRepository.findByIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found or access denied"));
        return new PlaylistResponse(playlist);
    }

    @Override
    public PlaylistResponse createPlaylist(Long userId, PlaylistRequest request) {
        Playlist playlist = new Playlist(userId, request.getName(), request.getDescription());
        Playlist saved = playlistRepository.save(playlist);
        return new PlaylistResponse(saved);
    }

    @Override
    public void deletePlaylist(Long playlistId, Long userId) {
        Playlist playlist = playlistRepository.findByIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found or access denied"));
        playlistRepository.delete(playlist);
    }

    @Override
    public PlaylistItemResponse addMovieToPlaylist(Long playlistId, Long userId, PlaylistItemRequest request) {
        Playlist playlist = playlistRepository.findByIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found or access denied"));

        if (playlistItemRepository.existsByPlaylistIdAndTmdbMovieId(playlistId, request.getTmdbMovieId())) {
            throw new IllegalStateException("Movie is already in this playlist");
        }

        PlaylistItem item = new PlaylistItem(
                playlist,
                request.getTmdbMovieId(),
                request.getTitle(),
                request.getPosterPath(),
                request.getRating()
        );

        playlist.addItem(item);
        PlaylistItem savedItem = playlistItemRepository.save(item);
        return new PlaylistItemResponse(savedItem);
    }

    @Override
    public void removeMovieFromPlaylist(Long playlistId, Long tmdbMovieId, Long userId) {
        playlistRepository.findByIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found or access denied"));

        playlistItemRepository.deleteByPlaylistIdAndTmdbMovieId(playlistId, tmdbMovieId);
    }
}