package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.Genre;
import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.domain.entity.UserDislikedGenre;
import com.example.movie_mood.repository.GenreRepository;
import com.example.movie_mood.repository.UserDislikedGenreRepository;
import com.example.movie_mood.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPreferenceServiceTest {

    private UserRepository userRepository;
    private GenreRepository genreRepository;
    private UserDislikedGenreRepository userDislikedGenreRepository;
    private UserPreferenceService userPreferenceService;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userRepository = mock(UserRepository.class);
        genreRepository = mock(GenreRepository.class);
        userDislikedGenreRepository =
                mock(UserDislikedGenreRepository.class);

        userPreferenceService = new UserPreferenceService(
                userRepository,
                genreRepository,
                userDislikedGenreRepository
        );
    }

    @Test
    void getDislikedGenreIdsShouldReturnGenreIds() {

        User user = new User(
                "testuser",
                "test@example.com",
                "hashedPassword"
        );
        user.setUserId(userId);

        Genre action = new Genre("28", "Action");
        Genre horror = new Genre("27", "Horror");

        UserDislikedGenre first =
                new UserDislikedGenre(user, action);

        UserDislikedGenre second =
                new UserDislikedGenre(user, horror);

        when(userRepository.existsById(userId))
                .thenReturn(true);

        when(userDislikedGenreRepository.findByUserUserId(userId))
                .thenReturn(List.of(first, second));

        List<String> result =
                userPreferenceService.getDislikedGenreIds(userId);

        assertEquals(
                List.of("28", "27"),
                result
        );
    }

    @Test
    void getDislikedGenreIdsShouldRejectUnknownUser() {

        UUID randomId = UUID.randomUUID();
        when(userRepository.existsById(randomId))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userPreferenceService
                                .getDislikedGenreIds(randomId)
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userDislikedGenreRepository, never())
                .findByUserUserId(any(UUID.class));
    }

    @Test
    void updateDislikedGenresShouldSavePreferences() {

        User user = new User(
                "testuser",
                "test@example.com",
                "hashedPassword"
        );
        user.setUserId(userId);

        Genre action = new Genre("28", "Action");
        Genre horror = new Genre("27", "Horror");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(genreRepository.findById("28"))
                .thenReturn(Optional.of(action));

        when(genreRepository.findById("27"))
                .thenReturn(Optional.of(horror));

        List<String> result =
                userPreferenceService.updateDislikedGenres(
                        userId,
                        List.of("28", "27")
                );

        assertEquals(
                List.of("28", "27"),
                result
        );

        verify(userDislikedGenreRepository)
                .deleteByUserUserId(userId);

        verify(userDislikedGenreRepository, times(2))
                .save(any(UserDislikedGenre.class));
    }

    @Test
    void updateDislikedGenresShouldRejectUnknownUser() {

        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userPreferenceService
                                .updateDislikedGenres(
                                        randomId,
                                        List.of("28")
                                )
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userDislikedGenreRepository, never())
                .deleteByUserUserId(any(UUID.class));

        verify(userDislikedGenreRepository, never())
                .save(any(UserDislikedGenre.class));
    }

    @Test
    void updateDislikedGenresShouldRejectUnknownGenre() {

        User user = new User(
                "testuser",
                "test@example.com",
                "hashedPassword"
        );
        user.setUserId(userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(genreRepository.findById("999"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userPreferenceService
                                .updateDislikedGenres(
                                        userId,
                                        List.of("999")
                                )
                );

        assertEquals(
                "Genre not found: 999",
                exception.getMessage()
        );
    }
}