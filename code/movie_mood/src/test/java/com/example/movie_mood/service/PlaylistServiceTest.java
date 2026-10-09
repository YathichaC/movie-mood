package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.Movielist;
import com.example.movie_mood.domain.entity.Playlist;
import com.example.movie_mood.dto.MovielistRequest;
import com.example.movie_mood.dto.MovielistResponse;
import com.example.movie_mood.dto.PlaylistMovieBatchRequest;
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

import java.util.List;
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
    void testUpdateMoviePlaylists_BatchAddAndRemove_Success() {
        UUID addPlaylistId = UUID.randomUUID();
        UUID removePlaylistId = UUID.randomUUID();
        Playlist addPlaylist = new Playlist(userId, "Add Playlist");
        addPlaylist.setPlaylistId(addPlaylistId);
        Playlist removePlaylist = new Playlist(userId, "Remove Playlist");
        removePlaylist.setPlaylistId(removePlaylistId);

        PlaylistMovieBatchRequest request = new PlaylistMovieBatchRequest();
        request.setTmdbMovieId("550");
        request.setAddToPlaylistIds(List.of(addPlaylistId));
        request.setRemoveFromPlaylistIds(List.of(removePlaylistId));

        when(playlistRepository.findByPlaylistIdAndUserId(addPlaylistId, userId))
                .thenReturn(Optional.of(addPlaylist));
        when(playlistRepository.findByPlaylistIdAndUserId(removePlaylistId, userId))
                .thenReturn(Optional.of(removePlaylist));

        when(movielistRepository.existsByPlaylist_PlaylistIdAndTmdbMovieId(addPlaylistId, "550"))
                .thenReturn(false);

        playlistService.updateMoviePlaylists(userId, request);

        verify(movielistRepository, times(1))
                .save(any(Movielist.class));
        verify(movielistRepository, times(1))
                .deleteByPlaylist_PlaylistIdAndTmdbMovieId(removePlaylistId, "550");
    }

    @Test
    void testGetUserPlaylistsForPicker_IncludesCurrentMovieFlag() {
        UUID playlistId = mockPlaylist.getPlaylistId();
        String movieId = "550";

        List<Object[]> pickerRows = new java.util.ArrayList<>();
        pickerRows.add(new Object[]{playlistId, "Favorite Movies", 3L});
        when(playlistRepository.findPickerDataByUserId(userId))
                .thenReturn(pickerRows);
        when(movielistRepository.findPlaylistIdsByTmdbMovieIdAndUserId(movieId, userId))
                .thenReturn(List.of(playlistId));

        List<com.example.movie_mood.dto.PlaylistPickerResponse> response =
                playlistService.getUserPlaylistsForPicker(userId, movieId);

        assertEquals(1, response.size());
        assertTrue(response.get(0).isContainsCurrentMovie());
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