package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.Playlist;
import com.example.movie_mood.domain.entity.PlaylistItem;
import com.example.movie_mood.dto.PlaylistItemRequest;
import com.example.movie_mood.dto.PlaylistItemResponse;
import com.example.movie_mood.dto.PlaylistRequest;
import com.example.movie_mood.dto.PlaylistResponse;
import com.example.movie_mood.repository.PlaylistItemRepository;
import com.example.movie_mood.repository.PlaylistRepository;
import com.example.movie_mood.service.impl.PlaylistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PlaylistItemRepository playlistItemRepository;

    @InjectMocks
    private PlaylistServiceImpl playlistService;

    private Long userId;
    private Playlist mockPlaylist;

    @BeforeEach
    void setUp() {
        userId = 1L;
        mockPlaylist = new Playlist(userId, "Favorite Movies", "My personal list");
        mockPlaylist.setId(10L);
    }

    @Test
    void testCreatePlaylist_Success() {
        PlaylistRequest request = new PlaylistRequest("Weekend Binge", "Chill vibes");
        when(playlistRepository.save(any(Playlist.class))).thenAnswer(invocation -> {
            Playlist p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        PlaylistResponse response = playlistService.createPlaylist(userId, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Weekend Binge", response.getName());
        verify(playlistRepository, times(1)).save(any(Playlist.class));
    }

    @Test
    void testAddMovieToPlaylist_Success() {
        Long playlistId = 10L;
        PlaylistItemRequest request = new PlaylistItemRequest(550L, "Fight Club", "/poster.jpg", 8.8);

        when(playlistRepository.findByIdAndUserId(playlistId, userId)).thenReturn(Optional.of(mockPlaylist));
        when(playlistItemRepository.existsByPlaylistIdAndTmdbMovieId(playlistId, 550L)).thenReturn(false);
        when(playlistItemRepository.save(any(PlaylistItem.class))).thenAnswer(invocation -> {
            PlaylistItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        PlaylistItemResponse response = playlistService.addMovieToPlaylist(playlistId, userId, request);

        assertNotNull(response);
        assertEquals("Fight Club", response.getTitle());
        assertEquals(550L, response.getTmdbMovieId());
        verify(playlistItemRepository, times(1)).save(any(PlaylistItem.class));
    }

    @Test
    void testAddMovieToPlaylist_DuplicateMovie_ThrowsException() {
        Long playlistId = 10L;
        PlaylistItemRequest request = new PlaylistItemRequest(550L, "Fight Club", "/poster.jpg", 8.8);

        when(playlistRepository.findByIdAndUserId(playlistId, userId)).thenReturn(Optional.of(mockPlaylist));
        when(playlistItemRepository.existsByPlaylistIdAndTmdbMovieId(playlistId, 550L)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            playlistService.addMovieToPlaylist(playlistId, userId, request);
        });

        assertEquals("Movie is already in this playlist", exception.getMessage());
        verify(playlistItemRepository, never()).save(any(PlaylistItem.class));
    }

    @Test
    void testDeletePlaylist_Success() {
        Long playlistId = 10L;
        when(playlistRepository.findByIdAndUserId(playlistId, userId)).thenReturn(Optional.of(mockPlaylist));

        playlistService.deletePlaylist(playlistId, userId);

        verify(playlistRepository, times(1)).delete(mockPlaylist);
    }
}