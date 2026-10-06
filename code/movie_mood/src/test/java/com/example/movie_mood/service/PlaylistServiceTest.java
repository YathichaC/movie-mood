package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.Movielist;
import com.example.movie_mood.domain.entity.Playlist;
import com.example.movie_mood.dto.MovielistRequest;
import com.example.movie_mood.dto.MovielistResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.repository.MovielistRepository;
import com.example.movie_mood.repository.PlaylistRepository;
import com.example.movie_mood.service.impl.PlaylistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private MovielistRepository movielistRepository;

    @InjectMocks
    private PlaylistServiceImpl playlistService;

    private UUID userId;
    private Playlist mockPlaylist;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        mockPlaylist = new Playlist(
                userId,
                "Favorite Movies");

        mockPlaylist.setPlaylistId(UUID.randomUUID());
    }

    @Test
    void testCreatePlaylist_Success() {
        PlaylistRequest request = new PlaylistRequest();
        request.setPlaylistName("Weekend Binge");
        request.setDetail("Movies to watch this weekend");
        request.setCoverImagePath("playlists/weekend.jpg");

        when(playlistRepository.save(any(Playlist.class)))
                .thenAnswer(invocation -> {
                    Playlist playlist = invocation.getArgument(0);
                    playlist.setPlaylistId(UUID.randomUUID());
                    return playlist;
                });

        PlaylistResponse response = playlistService.createPlaylist(userId, request);

        assertNotNull(response);
        assertNotNull(response.getPlaylistId());
        assertEquals("Weekend Binge", response.getPlaylistName());
        assertEquals("Movies to watch this weekend", response.getDetail());
        assertEquals("playlists/weekend.jpg", response.getCoverImagePath());

        verify(playlistRepository, times(1))
                .save(any(Playlist.class));
    }

    @Test
    void testAddMovieToPlaylist_Success() {
        UUID playlistId = mockPlaylist.getPlaylistId();

        MovielistRequest request = new MovielistRequest("550");

        when(playlistRepository
                .findByPlaylistIdAndUserId(playlistId, userId))
                .thenReturn(Optional.of(mockPlaylist));

        when(movielistRepository
                .existsByPlaylist_PlaylistIdAndTmdbMovieId(
                        playlistId,
                        "550"))
                .thenReturn(false);

        when(movielistRepository.save(any(Movielist.class)))
                .thenAnswer(invocation -> {
                    Movielist item = invocation.getArgument(0);
                    item.setId(UUID.randomUUID());
                    return item;
                });

        MovielistResponse response = playlistService.addMovieToPlaylist(
                playlistId,
                userId,
                request);

        assertNotNull(response);
        assertEquals("550", response.getTmdbMovieId());

        verify(movielistRepository, times(1))
                .save(any(Movielist.class));
    }

    @Test
    void testAddMovieToPlaylist_DuplicateMovie_ThrowsException() {
        UUID playlistId = mockPlaylist.getPlaylistId();

        MovielistRequest request = new MovielistRequest("550");

        when(playlistRepository
                .findByPlaylistIdAndUserId(playlistId, userId))
                .thenReturn(Optional.of(mockPlaylist));

        when(movielistRepository
                .existsByPlaylist_PlaylistIdAndTmdbMovieId(
                        playlistId,
                        "550"))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> playlistService.addMovieToPlaylist(
                        playlistId,
                        userId,
                        request));

        assertEquals(
                "Movie is already in this playlist",
                exception.getMessage());

        verify(movielistRepository, never())
                .save(any(Movielist.class));
    }

    @Test
    void testDeletePlaylist_Success() {
        UUID playlistId = mockPlaylist.getPlaylistId();

        when(playlistRepository
                .findByPlaylistIdAndUserId(playlistId, userId))
                .thenReturn(Optional.of(mockPlaylist));

        playlistService.deletePlaylist(
                playlistId,
                userId);

        verify(playlistRepository, times(1))
                .delete(mockPlaylist);
    }

    @Test
    void testCreatePlaylist_WithoutDetailAndCoverImage_Success() {
        PlaylistRequest request = new PlaylistRequest();
        request.setPlaylistName("Simple Playlist");

        when(playlistRepository.save(any(Playlist.class)))
                .thenAnswer(invocation -> {
                    Playlist playlist = invocation.getArgument(0);
                    playlist.setPlaylistId(UUID.randomUUID());
                    return playlist;
                });

        PlaylistResponse response = playlistService.createPlaylist(userId, request);

        assertNotNull(response);
        assertEquals("Simple Playlist", response.getPlaylistName());
        assertNull(response.getDetail());
        assertNull(response.getCoverImagePath());

        verify(playlistRepository, times(1))
                .save(any(Playlist.class));
    }
}