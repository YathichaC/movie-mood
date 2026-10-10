package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.PasswordResetToken;
import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PasswordResetTokenCleanupServiceTest {

    private PasswordResetTokenRepository tokenRepository;
    private PasswordResetTokenCleanupService cleanupService;

    @BeforeEach
    void setUp() {
        tokenRepository = mock(PasswordResetTokenRepository.class);
        cleanupService = new PasswordResetTokenCleanupService(tokenRepository);
    }

    @Test
    void purgeExpiredTokens_shouldDeleteTokensBeforeCurrentTime() {
        cleanupService.purgeExpiredTokens();

        verify(tokenRepository, times(1)).deleteByExpiryDateBefore(any(Instant.class));
    }
}
