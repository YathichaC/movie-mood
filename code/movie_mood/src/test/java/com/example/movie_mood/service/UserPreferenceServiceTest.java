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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPreferenceServiceTest {

    private UserRepository userRepository;
    private GenreRepository genreRepository;
    private UserDislikedGenreRepository userDislikedGenreRepository;
    private UserPreferenceService userPreferenceService;

    @BeforeEach
    void setUp() {
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

        Genre action = new Genre(28, "Action");
        Genre horror = new Genre(27, "Horror");

        UserDislikedGenre first =
                new UserDislikedGenre(user, action);

        UserDislikedGenre second =
                new UserDislikedGenre(user, horror);

        when(userRepository.existsById(1))
                .thenReturn(true);

        when(userDislikedGenreRepository.findByUserUserId(1))
                .thenReturn(List.of(first, second));

        List<Integer> result =
                userPreferenceService.getDislikedGenreIds(1);

        assertEquals(
                List.of(28, 27),
                result
        );
    }

    @Test
    void getDislikedGenreIdsShouldRejectUnknownUser() {

        when(userRepository.existsById(999))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userPreferenceService
                                .getDislikedGenreIds(999)
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userDislikedGenreRepository, never())
                .findByUserUserId(anyInt());
    }

    @Test
    void updateDislikedGenresShouldSavePreferences() {

        User user = new User(
                "testuser",
                "test@example.com",
                "hashedPassword"
        );

        Genre action = new Genre(28, "Action");
        Genre horror = new Genre(27, "Horror");

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        when(genreRepository.findById(28))
                .thenReturn(Optional.of(action));

        when(genreRepository.findById(27))
                .thenReturn(Optional.of(horror));

        List<Integer> result =
                userPreferenceService.updateDislikedGenres(
                        1,
                        List.of(28, 27)
                );

        assertEquals(
                List.of(28, 27),
                result
        );

        verify(userDislikedGenreRepository)
                .deleteByUserUserId(1);

        verify(userDislikedGenreRepository, times(2))
                .save(any(UserDislikedGenre.class));
    }

    @Test
    void updateDislikedGenresShouldRejectUnknownUser() {

        when(userRepository.findById(999))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userPreferenceService
                                .updateDislikedGenres(
                                        999,
                                        List.of(28)
                                )
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userDislikedGenreRepository, never())
                .deleteByUserUserId(anyInt());

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

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        when(genreRepository.findById(999))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userPreferenceService
                                .updateDislikedGenres(
                                        1,
                                        List.of(999)
                                )
                );

        assertEquals(
                "Genre not found: 999",
                exception.getMessage()
        );
    }
}