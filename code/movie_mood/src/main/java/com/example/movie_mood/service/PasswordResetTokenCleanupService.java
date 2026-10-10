package com.example.movie_mood.service;

import com.example.movie_mood.repository.PasswordResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PasswordResetTokenCleanupService {

    private final PasswordResetTokenRepository tokenRepository;

    public PasswordResetTokenCleanupService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    @Transactional
    public void purgeExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}
