package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.entity.Playlist;
import com.example.movie_mood.domain.entity.Movielist;
import com.example.movie_mood.dto.MovielistRequest;
import com.example.movie_mood.dto.MovielistResponse;
import com.example.movie_mood.dto.PlaylistMovieBatchRequest;
import com.example.movie_mood.dto.PlaylistPickerResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.dto.PlaylistSummaryResponse;
import com.example.movie_mood.repository.MovielistRepository;
import com.example.movie_mood.repository.PlaylistRepository;
import com.example.movie_mood.service.PlaylistService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.movie_mood.domain.entity.PlaylistDetail;

import java.util.UUID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final MovielistRepository movielistRepository;

    public PlaylistServiceImpl(
            PlaylistRepository playlistRepository,
            MovielistRepository movielistRepository) {

        this.playlistRepository = playlistRepository;
        this.movielistRepository = movielistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaylistSummaryResponse> getUserPlaylists(UUID userId) {

        return playlistRepository.findSummaryByUserId(userId).stream()
                .map(row -> {
                    UUID playlistId = (UUID) row[0];
                    String playlistName = (String) row[1];
                    String coverImagePath = row[2] == null ? null : row[2].toString();
                    long itemCount = row[3] == null ? 0L : ((Number) row[3]).longValue();

                    return new PlaylistSummaryResponse(
                            playlistId,
                            playlistName,
                            coverImagePath,
                            Math.toIntExact(itemCount));
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaylistPickerResponse> getUserPlaylistsForPicker(UUID userId, String tmdbMovieId) {
        Set<UUID> playlistsContainingMovie = new HashSet<>();
        if (tmdbMovieId != null && !tmdbMovieId.isBlank()) {
            String normalizedMovieId = tmdbMovieId.trim();
            playlistsContainingMovie.addAll(
                    movielistRepository.findPlaylistIdsByTmdbMovieIdAndUserId(normalizedMovieId, userId)
            );
        }

        return playlistRepository.findPickerDataByUserId(userId).stream()
                .map(row -> {
                    UUID playlistId = (UUID) row[0];
                    String playlistName = (String) row[1];
                    long itemCount = row[2] == null ? 0L : ((Number) row[2]).longValue();

                    return new PlaylistPickerResponse(
                            playlistId,
                            playlistName,
                            Math.toIntExact(itemCount),
                            playlistsContainingMovie.contains(playlistId));
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistResponse getPlaylistDetail(
            UUID playlistId,
            UUID userId) {

        Playlist playlist = requireOwnedPlaylist(playlistId, userId);

        return new PlaylistResponse(playlist);
    }

    @Override
    public PlaylistResponse createPlaylist(
            UUID userId,
            PlaylistRequest request) {

        Playlist playlist = new Playlist(
                userId,
                request.getPlaylistName());

        PlaylistDetail detail = new PlaylistDetail(
                playlist,
                request.getDetail(),
                request.getCoverImagePath());

        playlist.setDetail(detail);

        Playlist saved = playlistRepository.save(playlist);

        return new PlaylistResponse(saved);
    }

    @Override
    public PlaylistResponse updatePlaylist(
            UUID playlistId,
            UUID userId,
            PlaylistRequest request) {

        Playlist playlist = requireOwnedPlaylist(playlistId, userId);

        playlist.setPlaylistName(
                request.getPlaylistName());

        PlaylistDetail detail = playlist.getDetail();

        if (detail == null) {

            detail = new PlaylistDetail(
                    playlist,
                    request.getDetail(),
                    request.getCoverImagePath());

            playlist.setDetail(detail);

        } else {

            if (request.getDetail() != null) {
                detail.setDetail(
                        request.getDetail());
            }

            if (request.getCoverImagePath() != null) {
                detail.setCoverImagePath(
                        request.getCoverImagePath());
            }
        }

        Playlist saved = playlistRepository.save(playlist);

        return new PlaylistResponse(saved);
    }

    @Override
    public void deletePlaylist(
            UUID playlistId,
            UUID userId) {

        Playlist playlist = requireOwnedPlaylist(playlistId, userId);

        playlistRepository.delete(playlist);
    }

    @Override
    public MovielistResponse addMovieToPlaylist(
            UUID playlistId,
            UUID userId,
            MovielistRequest request) {

        Playlist playlist = requireOwnedPlaylist(playlistId, userId);

        if (movielistRepository
                .existsByPlaylist_PlaylistIdAndTmdbMovieId(
                        playlistId,
                        request.getTmdbMovieId())) {

            throw new IllegalStateException(
                    "Movie is already in this playlist");
        }

        Movielist item = new Movielist(
                playlist,
                request.getTmdbMovieId());

        playlist.addItem(item);

        Movielist savedItem = movielistRepository.save(item);

        return new MovielistResponse(savedItem);
    }

    @Override
    public void updateMoviePlaylists(
            UUID userId,
            PlaylistMovieBatchRequest request) {

        if (request == null || request.getTmdbMovieId() == null || request.getTmdbMovieId().isBlank()) {
            throw new IllegalArgumentException("tmdbMovieId is required");
        }

        String tmdbMovieId = request.getTmdbMovieId().trim();

        Set<UUID> addToPlaylistIds = new HashSet<>(
                request.getAddToPlaylistIds() == null
                        ? List.of()
                        : request.getAddToPlaylistIds());
        Set<UUID> removeFromPlaylistIds = new HashSet<>(
                request.getRemoveFromPlaylistIds() == null
                        ? List.of()
                        : request.getRemoveFromPlaylistIds());

        addToPlaylistIds.removeAll(removeFromPlaylistIds);

        Set<UUID> allPlaylistIds = new HashSet<>();
        allPlaylistIds.addAll(addToPlaylistIds);
        allPlaylistIds.addAll(removeFromPlaylistIds);

        Map<UUID, Playlist> authorizedPlaylists = new HashMap<>();
        for (UUID playlistId : allPlaylistIds) {
            Playlist playlist = requireOwnedPlaylist(playlistId, userId);

            authorizedPlaylists.put(playlistId, playlist);
        }

        for (UUID playlistId : removeFromPlaylistIds) {
            movielistRepository.deleteByPlaylist_PlaylistIdAndTmdbMovieId(
                    playlistId,
                    tmdbMovieId);
        }

        for (UUID playlistId : addToPlaylistIds) {
            if (!movielistRepository.existsByPlaylist_PlaylistIdAndTmdbMovieId(
                    playlistId,
                    tmdbMovieId)) {

                Movielist item = new Movielist(
                        authorizedPlaylists.get(playlistId),
                        tmdbMovieId);

                movielistRepository.save(item);
            }
        }
    }

    @Override
    public void removeMovieFromPlaylist(
            UUID playlistId,
            String tmdbMovieId,
            UUID userId) {

        requireOwnedPlaylist(playlistId, userId);

        movielistRepository
                .deleteByPlaylist_PlaylistIdAndTmdbMovieId(
                        playlistId,
                        tmdbMovieId);

    }

    @Override
    public String deletePlaylistImage(
            UUID playlistId,
            UUID userId) {

        Playlist playlist = requireOwnedPlaylist(playlistId, userId);

        PlaylistDetail detail = playlist.getDetail();
        String oldImagePath = detail != null ? detail.getCoverImagePath() : null;

        if (detail != null) {
            detail.setCoverImagePath(null);
            playlistRepository.save(playlist);
        }

        return oldImagePath;
    }

    private Playlist requireOwnedPlaylist(UUID playlistId, UUID userId) {
        return playlistRepository.findByPlaylistIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new AccessDeniedException("Playlist not found or access denied"));
    }
}