package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.dto.auth.LoginRequest;
import com.example.movie_mood.dto.auth.RegisterRequest;
import com.example.movie_mood.repository.PasswordResetTokenRepository;
import com.example.movie_mood.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordResetTokenRepository tokenRepository;
    private JavaMailSender mailSender;
    private AuthService authService;

    @Mock
    private TemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(PasswordResetTokenRepository.class);
        mailSender = mock(JavaMailSender.class);
        templateEngine = mock(TemplateEngine.class);
        authService = new AuthService(userRepository, tokenRepository, mailSender, templateEngine); 
    }

    @Test
    void registerShouldSaveUserWithHashedPassword() {
        RegisterRequest request = createRequest(
                "testuser",
                "test@example.com",
                "Password123!"
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);
        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(request);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertNotEquals("Password123!", savedUser.getPassword());
        assertTrue(savedUser.getPassword().startsWith("$2"));
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        RegisterRequest request = createRequest(
                "testuser",
                "test@example.com",
                "Password123!"
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Email is already registered",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerShouldRejectDuplicateUsername() {
        RegisterRequest request = createRequest(
                "testuser",
                "test@example.com",
                "Password123!"
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);
        when(userRepository.existsByUsername("testuser"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Username is already taken",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldReturnUserWhenCredentialsAreCorrect() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = new User(
                "testuser",
                "test@example.com",
                encoder.encode("Password123!")
        );

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Password123!");

        User result = authService.login(request);

        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void loginShouldAcceptNewPasswordAndRejectOldPasswordAfterUpdate() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String oldPassword = "OldPassword123!";
        String newPassword = "NewPassword123!";

        User user = new User(
                "testuser",
                "test@example.com",
                encoder.encode(oldPassword)
        );

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        user.setPassword(encoder.encode(newPassword));

        LoginRequest newPasswordRequest = new LoginRequest();
        newPasswordRequest.setUsername("testuser");
        newPasswordRequest.setPassword(newPassword);

        User result = authService.login(newPasswordRequest);
        assertEquals("testuser", result.getUsername());

        LoginRequest oldPasswordRequest = new LoginRequest();
        oldPasswordRequest.setUsername("testuser");
        oldPasswordRequest.setPassword(oldPassword);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(oldPasswordRequest)
        );

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void loginShouldRejectIncorrectPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = new User(
                "testuser",
                "test@example.com",
                encoder.encode("Password123!")
        );

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongPassword");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals(
                "Invalid username or password",
                exception.getMessage()
        );
    }

    @Test
    void loginShouldRejectUnknownUsername() {
        when(userRepository.findByUsername("unknownuser"))
                .thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setUsername("unknownuser");
        request.setPassword("Password123!");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals(
                "Invalid username or password",
                exception.getMessage()
        );
    }

    private RegisterRequest createRequest(
            String username,
            String email,
            String password) {

        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }
}
