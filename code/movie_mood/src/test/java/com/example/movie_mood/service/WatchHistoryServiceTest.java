package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.WatchHistory;
import com.example.movie_mood.dto.WatchHistoryRequest;
import com.example.movie_mood.dto.WatchHistoryResponse;
import com.example.movie_mood.repository.WatchHistoryRepository;
import com.example.movie_mood.service.impl.WatchHistoryServiceImpl;
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
class WatchHistoryServiceTest {

    @Mock
    private WatchHistoryRepository watchHistoryRepository;

    @InjectMocks
    private WatchHistoryServiceImpl watchHistoryService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void testRecordWatchedMovie_NewMovie_Success() {
        WatchHistoryRequest request = new WatchHistoryRequest("550");

        when(watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, "550")).thenReturn(Optional.empty());
        when(watchHistoryRepository.save(any(WatchHistory.class))).thenAnswer(invocation -> {
            WatchHistory saved = invocation.getArgument(0);
            if (saved.getHistoryId() == null) {
                saved.setHistoryId(UUID.randomUUID());
            }
            return saved;
        });

        WatchHistoryResponse response = watchHistoryService.recordWatchedMovie(userId, request);

        assertNotNull(response);
        assertNotNull(response.getHistoryId());
        assertEquals("550", response.getTmdbMovieId());
        verify(watchHistoryRepository, times(1)).save(any(WatchHistory.class));
    }

    @Test
    void testRecordWatchedMovie_AlreadyWatched_UpdatesTime() {
        WatchHistory existingHistory = new WatchHistory(userId, "550");
        existingHistory.setHistoryId(UUID.randomUUID());
        WatchHistoryRequest request = new WatchHistoryRequest("550");

        when(watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, "550")).thenReturn(Optional.of(existingHistory));
        when(watchHistoryRepository.save(any(WatchHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WatchHistoryResponse response = watchHistoryService.recordWatchedMovie(userId, request);

        assertNotNull(response);
        assertEquals(existingHistory.getHistoryId(), response.getHistoryId());
        verify(watchHistoryRepository, times(1)).save(existingHistory);
    }

    @Test
    void testGetUserWatchHistory_ReturnsList() {
        WatchHistory movieA = new WatchHistory(userId, "101");
        WatchHistory movieB = new WatchHistory(userId, "102");

        when(watchHistoryRepository.findByUserId(userId)).thenReturn(List.of(movieA, movieB));

        List<WatchHistoryResponse> result = watchHistoryService.getUserWatchHistory(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("101", result.get(0).getTmdbMovieId());
        assertEquals("102", result.get(1).getTmdbMovieId());
        verify(watchHistoryRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testIsMovieWatched_TrueAndFalse() {
        WatchHistory movie = new WatchHistory(userId, "550");
        when(watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, "550")).thenReturn(Optional.of(movie));
        when(watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, "999")).thenReturn(Optional.empty());

        assertTrue(watchHistoryService.isMovieWatched(userId, "550"));
        assertFalse(watchHistoryService.isMovieWatched(userId, "999"));
    }

    @Test
    void testRemoveWatchedMovie_Success() {
        watchHistoryService.removeWatchedMovie(userId, "550");

        verify(watchHistoryRepository, times(1)).deleteByUserIdAndTmdbMovieId(userId, "550");
    }
}