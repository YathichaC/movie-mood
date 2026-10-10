package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.dto.user.ChangePasswordRequest;
import com.example.movie_mood.dto.user.UpdateProfileRequest;
import com.example.movie_mood.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        UUID currentUserId = getCurrentUserId(authentication);

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated"));
        }

        try {
            User updatedUser = userService.updateProfile(
                    currentUserId,
                    request);

            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully",
                    "userId", updatedUser.getUserId(),
                    "username", updatedUser.getUsername(),
                    "email", updatedUser.getEmail()));

        } catch (IllegalArgumentException e) {

            if (e.getMessage().contains("already")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", e.getMessage()));
            }

            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePasswordPatch(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        return processPasswordChange(request, authentication);
    }

    private ResponseEntity<?> processPasswordChange(
            ChangePasswordRequest request,
            Authentication authentication) {

        UUID currentUserId = getCurrentUserId(authentication);

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated"));
        }

        try {
            userService.changePassword(currentUserId, request);

            return ResponseEntity.ok(
                    Map.of("message", "Password changed successfully"));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(
            Authentication authentication) {

        UUID currentUserId = getCurrentUserId(authentication);

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated"));
        }

        try {
            userService.deleteAccount(currentUserId);

            return ResponseEntity.ok(
                    Map.of("message", "Account deleted successfully"));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    private UUID getCurrentUserId(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            return null;
        }

        try {
            return UUID.fromString(authentication.getName());

        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}