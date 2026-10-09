package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.dto.user.ChangePasswordRequest;
import com.example.movie_mood.dto.user.UpdateProfileRequest;
import com.example.movie_mood.repository.PlaylistRepository;
import com.example.movie_mood.repository.UserDislikedGenreRepository;
import com.example.movie_mood.repository.UserRepository;
import com.example.movie_mood.repository.WatchHistoryRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDislikedGenreRepository userDislikedGenreRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlaylistRepository playlistRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final PlaylistImageService playlistImageService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public UserService(
            UserRepository userRepository,
            UserDislikedGenreRepository userDislikedGenreRepository,
            PlaylistRepository playlistRepository,
            WatchHistoryRepository watchHistoryRepository,
            PasswordEncoder passwordEncoder,
            PlaylistImageService playlistImageService) {

        this.userRepository = userRepository;
        this.userDislikedGenreRepository = userDislikedGenreRepository;
        this.playlistRepository = playlistRepository;
        this.watchHistoryRepository = watchHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.playlistImageService = playlistImageService;
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public User updateProfile(
            UUID userId,
            UpdateProfileRequest request) {

        User user = getUserById(userId);

        if (request.getUsername() != null) {
            String newUsername = request.getUsername().trim();

            if (newUsername.isEmpty()) {
                throw new IllegalArgumentException(
                        "Username cannot be empty");
            }

            if (newUsername.length() < 3
                    || newUsername.length() > 50) {
                throw new IllegalArgumentException(
                        "Username must be between 3 and 50 characters");
            }

            if (!newUsername.equalsIgnoreCase(user.getUsername())) {
                if (userRepository.existsByUsername(newUsername)) {
                    throw new IllegalArgumentException(
                            "Username is already taken");
                }

                user.setUsername(newUsername);
            }
        }

        if (request.getEmail() != null) {
            String newEmail = request.getEmail().trim();

            if (newEmail.isEmpty()) {
                throw new IllegalArgumentException(
                        "Email cannot be empty");
            }

            if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
                throw new IllegalArgumentException(
                        "Invalid email format");
            }

            if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new IllegalArgumentException(
                            "Email is already registered");
                }

                user.setEmail(newEmail);
            }
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(
            UUID userId,
            ChangePasswordRequest request) {

        User user = getUserById(userId);

        String newPassword = request.getNewPassword();
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "New password cannot be blank");
        }

        String trimmedPassword = newPassword.trim();
        if (!trimmedPassword.matches(
                "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,72}$")) {
            throw new IllegalArgumentException(
                    "Password must include uppercase, lowercase, a number, and a special character");
        }

        if (passwordEncoder.matches(trimmedPassword, user.getPassword())) {
            throw new IllegalArgumentException(
                    "New password cannot be the same as current password");
        }

        user.setPassword(passwordEncoder.encode(trimmedPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User user = getUserById(userId);

        var playlists = playlistRepository.findByUserId(userId);

        var coverImagePaths = playlists.stream()
                .map(playlist -> playlist.getDetail())
                .filter(detail -> detail != null)
                .map(detail -> detail.getCoverImagePath())
                .filter(path -> path != null && !path.isBlank())
                .distinct()
                .toList();

        userDislikedGenreRepository.deleteByUserUserId(userId);

        watchHistoryRepository.deleteByUserId(userId);

        playlistRepository.deleteAll(playlists);

        userRepository.delete(user);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        for (String imagePath : coverImagePaths) {
                            try {
                                playlistImageService.deleteImage(imagePath);
                            } catch (Exception exception) {
                                System.err.println(
                                        "Failed to delete playlist cover: "
                                                + exception.getMessage());
                            }
                        }
                    }
                });
    }
}
