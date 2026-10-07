package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.dto.user.ChangePasswordRequest;
import com.example.movie_mood.dto.user.UpdateProfileRequest;
import com.example.movie_mood.repository.PlaylistRepository;
import com.example.movie_mood.repository.UserDislikedGenreRepository;
import com.example.movie_mood.repository.UserRepository;
import com.example.movie_mood.repository.WatchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserDislikedGenreRepository userDislikedGenreRepository;
    private PasswordEncoder passwordEncoder;
    private PlaylistRepository playlistRepository;
    private WatchHistoryRepository watchHistoryRepository;
    private UserService userService;
    private UUID userId;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userRepository = mock(UserRepository.class);
        userDislikedGenreRepository = mock(UserDislikedGenreRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        playlistRepository = mock(PlaylistRepository.class);
        watchHistoryRepository = mock(WatchHistoryRepository.class);

        userService = new UserService(
                userRepository,
                userDislikedGenreRepository,
                playlistRepository,
                watchHistoryRepository,
                passwordEncoder);

        existingUser = new User(
                "old_username",
                "old@example.com",
                "encodedPassword123");

        existingUser.setUserId(userId);
    }

    @Test
    void updateProfileShouldUpdateOnlyUsernameWhenEmailIsNull() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByUsername("new_username"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("new_username");

        User result = userService.updateProfile(userId, request);

        assertEquals("new_username", result.getUsername());
        assertEquals("old@example.com", result.getEmail());

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateProfileShouldUpdateOnlyEmailWhenUsernameIsNull() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByEmail("new@example.com"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("new@example.com");

        User result = userService.updateProfile(userId, request);

        assertEquals("old_username", result.getUsername());
        assertEquals("new@example.com", result.getEmail());

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateProfileShouldRejectDuplicateUsername() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByUsername("taken_username"))
                .thenReturn(true);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("taken_username");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateProfile(userId, request));

        assertEquals(
                "Username is already taken",
                exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfileShouldRejectInvalidEmailFormat() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("invalid-email-format");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateProfile(userId, request));

        assertEquals(
                "Invalid email format",
                exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePasswordShouldSucceedWhenCurrentPasswordMatches() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(passwordEncoder.matches(
                "CurrentPass123!",
                "encodedPassword123"))
                .thenReturn(true);

        when(passwordEncoder.matches(
                "NewPass12345!",
                "encodedPassword123"))
                .thenReturn(false);

        when(passwordEncoder.encode("NewPass12345!"))
                .thenReturn("newEncodedPassword123");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("CurrentPass123!");
        request.setNewPassword("NewPass12345!");

        userService.changePassword(userId, request);

        assertEquals(
                "newEncodedPassword123",
                existingUser.getPassword());

        verify(userRepository).save(existingUser);
    }

    @Test
    void changePasswordShouldRejectWhenCurrentPasswordIsIncorrect() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(passwordEncoder.matches(
                "WrongPass123!",
                "encodedPassword123"))
                .thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("WrongPass123!");
        request.setNewPassword("NewPass12345!");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.changePassword(userId, request));

        assertEquals(
                "Current password is incorrect",
                exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteAccountShouldSucceedAndRemoveUserData() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(playlistRepository.findByUserId(userId))
                .thenReturn(Collections.emptyList());

        userService.deleteAccount(userId);

        verify(userDislikedGenreRepository)
                .deleteByUserUserId(userId);

        verify(watchHistoryRepository)
                .deleteByUserId(userId);

        verify(playlistRepository)
                .findByUserId(userId);

        verify(playlistRepository)
                .deleteAll(Collections.emptyList());

        verify(userRepository)
                .delete(existingUser);
    }
}