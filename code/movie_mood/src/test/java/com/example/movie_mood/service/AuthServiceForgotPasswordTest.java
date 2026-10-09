package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.PasswordResetToken;
import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.dto.auth.ForgotPasswordRequest;
import com.example.movie_mood.dto.auth.ResetPasswordRequest;
import com.example.movie_mood.repository.PasswordResetTokenRepository;
import com.example.movie_mood.repository.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceForgotPasswordTest {

    private UserRepository userRepository;
    private PasswordResetTokenRepository tokenRepository;
    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(PasswordResetTokenRepository.class);
        mailSender = mock(JavaMailSender.class);
        templateEngine = mock(TemplateEngine.class);
        authService = new AuthService(userRepository, tokenRepository, mailSender, templateEngine);
    }

    @Test
    void processForgotPassword_whenEmailExists_shouldCreateToken() {
        User user = new User("testuser", "user@example.com", "hashedOldPass");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(templateEngine.process(eq("mail/reset-password-email"), any(Context.class)))
                .thenReturn("<html>mock html</html>");
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("user@example.com");

        authService.processForgotPassword(req);

        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void processForgotPassword_whenEmailDoesNotExist_shouldNotThrowAndNotCreateToken() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("notfound@example.com");

        assertDoesNotThrow(() -> authService.processForgotPassword(req));
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void resetPassword_withValidToken_shouldUpdatePasswordAndInvalidateToken() {
        User user = new User(
                "testuser",
                "user@example.com",
                new BCryptPasswordEncoder().encode("OldPassword123!"));
        PasswordResetToken resetToken = new PasswordResetToken(
                "valid-token",
                user,
                Instant.now().plus(1, ChronoUnit.HOURS)
        );

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(resetToken));

        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("valid-token");
        req.setNewPassword("NewPassword123!");

        authService.resetPassword(req);

        assertTrue(resetToken.isUsed());
        assertTrue(new BCryptPasswordEncoder().matches("NewPassword123!", user.getPassword()));
        assertFalse(new BCryptPasswordEncoder().matches("OldPassword123!", user.getPassword()));
        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).save(resetToken);
    }

    @Test
    void resetPassword_withExpiredToken_shouldThrowException() {
        User user = new User("testuser", "user@example.com", "hashedOldPass");
        PasswordResetToken resetToken = new PasswordResetToken(
                "expired-token",
                user,
                Instant.now().minus(10, ChronoUnit.MINUTES) 
        );

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(resetToken));

        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("expired-token");
        req.setNewPassword("NewPassword123!");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.resetPassword(req)
        );

        assertEquals("Token has expired", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_withAlreadyUsedToken_shouldThrowException() {
        User user = new User("testuser", "user@example.com", "hashedOldPass");
        PasswordResetToken resetToken = new PasswordResetToken(
                "used-token",
                user,
                Instant.now().plus(1, ChronoUnit.HOURS)
        );
        resetToken.setUsed(true); 

        when(tokenRepository.findByToken("used-token")).thenReturn(Optional.of(resetToken));

        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("used-token");
        req.setNewPassword("NewPassword123!");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.resetPassword(req)
        );

        assertEquals("Token has already been used", ex.getMessage());
    }
}