package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.dto.auth.LoginRequest;
import com.example.movie_mood.dto.auth.RegisterRequest;
import com.example.movie_mood.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

class AuthServiceTest {

    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthService(userRepository);
    }

    @Test
    void registerShouldSaveUserWithHashedPassword() {
        RegisterRequest request = createRequest(
                "testuser",
                "test@example.com",
                "password123",
                "password123"
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

        assertNotEquals(
                "password123",
                savedUser.getPassword()
        );

        assertTrue(
                savedUser.getPassword().startsWith("$2")
        );
    }

    @Test
    void registerShouldRejectPasswordMismatch() {
        RegisterRequest request = createRequest(
                "testuser",
                "test@example.com",
                "password123",
                "differentPassword"
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "Passwords do not match",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        RegisterRequest request = createRequest(
                "testuser",
                "test@example.com",
                "password123",
                "password123"
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "Email is already registered",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void registerShouldRejectDuplicateUsername() {
        RegisterRequest request = createRequest(
                "testuser",
                "test@example.com",
                "password123",
                "password123"
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "Username is already taken",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
void loginShouldReturnUserWhenCredentialsAreCorrect() {

    BCryptPasswordEncoder encoder =
        new BCryptPasswordEncoder();

    User user = new User(
        "testuser",
        "test@example.com",
        encoder.encode("password123")
);

    when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(user));

    LoginRequest request = new LoginRequest();
    request.setEmail("test@example.com");
    request.setPassword("password123");

    User result = authService.login(request);

    assertEquals("testuser", result.getUsername());
    assertEquals("test@example.com", result.getEmail());
}

    @Test
void loginShouldRejectIncorrectPassword() {

    BCryptPasswordEncoder encoder =
        new BCryptPasswordEncoder();

    User user = new User(
        "testuser",
        "test@example.com",
        encoder.encode("password123")
);

    when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(user));

    LoginRequest request = new LoginRequest();
    request.setEmail("test@example.com");
    request.setPassword("wrongPassword");

    IllegalArgumentException exception =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.login(request)
            );

    assertEquals(
            "Invalid email or password",
            exception.getMessage()
    );
}

    @Test
void loginShouldRejectUnknownEmail() {

    when(userRepository.findByEmail("unknown@example.com"))
            .thenReturn(Optional.empty());

    LoginRequest request = new LoginRequest();
    request.setEmail("unknown@example.com");
    request.setPassword("password123");

    IllegalArgumentException exception =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.login(request)
            );

    assertEquals(
            "Invalid email or password",
            exception.getMessage()
    );
}

    private RegisterRequest createRequest(
            String username,
            String email,
            String password,
            String confirmPassword) {

        RegisterRequest request = new RegisterRequest();

        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);

        return request;
    }
}