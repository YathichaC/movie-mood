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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchHistoryServiceTest {

    @Mock
    private WatchHistoryRepository watchHistoryRepository;

    @InjectMocks
    private WatchHistoryServiceImpl watchHistoryService;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    @Test
    void testRecordWatchedMovie_NewMovie_Success() {
        WatchHistoryRequest request = new WatchHistoryRequest(550L, "Fight Club", "/poster.jpg");

        when(watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, 550L)).thenReturn(Optional.empty());
        when(watchHistoryRepository.save(any(WatchHistory.class))).thenAnswer(invocation -> {
            WatchHistory saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        WatchHistoryResponse response = watchHistoryService.recordWatchedMovie(userId, request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Fight Club", response.getTitle());
        assertEquals(550L, response.getTmdbMovieId());
        verify(watchHistoryRepository, times(1)).save(any(WatchHistory.class));
    }

    @Test
    void testRecordWatchedMovie_AlreadyWatched_UpdatesTime() {
        // Arrange
        WatchHistory existingHistory = new WatchHistory(userId, 550L, "Fight Club", "/poster.jpg");
        existingHistory.setId(5L);
        WatchHistoryRequest request = new WatchHistoryRequest(550L, "Fight Club", "/poster.jpg");

        when(watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, 550L)).thenReturn(Optional.of(existingHistory));
        when(watchHistoryRepository.save(any(WatchHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WatchHistoryResponse response = watchHistoryService.recordWatchedMovie(userId, request);

        assertNotNull(response);
        assertEquals(5L, response.getId());
        verify(watchHistoryRepository, times(1)).save(existingHistory);
    }

    @Test
    void testGetUserWatchHistory_ReturnsSortedList() {
        WatchHistory movieA = new WatchHistory(userId, 101L, "Avatar", "/avatar.jpg");
        WatchHistory movieB = new WatchHistory(userId, 102L, "Batman", "/batman.jpg");

        when(watchHistoryRepository.findByUserIdOrderByTitleAsc(userId)).thenReturn(List.of(movieA, movieB));

        List<WatchHistoryResponse> result = watchHistoryService.getUserWatchHistory(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Avatar", result.get(0).getTitle());
        assertEquals("Batman", result.get(1).getTitle());
        verify(watchHistoryRepository, times(1)).findByUserIdOrderByTitleAsc(userId);
    }

    @Test
    void testIsMovieWatched_TrueAndFalse() {
        WatchHistory movie = new WatchHistory(userId, 550L, "Fight Club", "/poster.jpg");
        when(watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, 550L)).thenReturn(Optional.of(movie));
        when(watchHistoryRepository.findByUserIdAndTmdbMovieId(userId, 999L)).thenReturn(Optional.empty());

        assertTrue(watchHistoryService.isMovieWatched(userId, 550L));
        assertFalse(watchHistoryService.isMovieWatched(userId, 999L));
    }

    @Test
    void testRemoveWatchedMovie_Success() {

        watchHistoryService.removeWatchedMovie(userId, 550L);

        verify(watchHistoryRepository, times(1)).deleteByUserIdAndTmdbMovieId(userId, 550L);
    }
}