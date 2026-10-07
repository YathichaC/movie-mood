package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.dto.user.DeleteAccountRequest;
import com.example.movie_mood.dto.user.UpdateProfileRequest;
import com.example.movie_mood.repository.UserDislikedGenreRepository;
import com.example.movie_mood.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDislikedGenreRepository userDislikedGenreRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserDislikedGenreRepository userDislikedGenreRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userDislikedGenreRepository = userDislikedGenreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public User updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = getUserById(userId);

        if (!user.getUsername().equalsIgnoreCase(request.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUsername(request.getUsername());
        }

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email is already registered");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(UUID userId, DeleteAccountRequest request) {
        User user = getUserById(userId);

        if (!user.getUsername().equals(request.getUsername().trim())) {
            throw new IllegalArgumentException("Username does not match");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        userDislikedGenreRepository.deleteByUserUserId(userId);

        userRepository.delete(user);
    }
}