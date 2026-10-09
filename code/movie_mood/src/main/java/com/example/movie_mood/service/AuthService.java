package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.PasswordResetToken;
import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.dto.auth.ForgotPasswordRequest;
import com.example.movie_mood.dto.auth.LoginRequest;
import com.example.movie_mood.dto.auth.RegisterRequest;
import com.example.movie_mood.dto.auth.ResetPasswordRequest;
import com.example.movie_mood.repository.PasswordResetTokenRepository;
import com.example.movie_mood.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username:moviemood8080@gmail.com}")
    private String mailUsername = "moviemood8080@gmail.com";

    @org.springframework.beans.factory.annotation.Autowired
    public AuthService(UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailService = emailService;
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthService.class);

    @Transactional
    public boolean processForgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail().trim());

        if (userOptional.isEmpty()) {
            log.info("Forgot password: no matching account found");
            return false;
        }

        log.info("Forgot password: matching account found");

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(10, ChronoUnit.MINUTES);

        PasswordResetToken resetToken = tokenRepository.findByUser(user)
                .orElse(new PasswordResetToken());

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiryDate);
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        String resetLink = baseUrl + "/auth/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("resetUrl", resetLink);

        String emailContent = templateEngine.process("mail/reset-password-email",
                context);
        emailService.sendResetPasswordEmail(user.getEmail(), emailContent);
        return true;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Token has already been used");
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        String newPassword = request.getNewPassword() != null ? request.getNewPassword().trim() : "";

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        boolean hasUpper = newPassword.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = newPassword.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = newPassword.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = newPassword.matches(".*[^A-Za-z0-9].*");

        if (!hasUpper || !hasLower) {
            throw new IllegalArgumentException("Password must contain both uppercase and lowercase letters");
        }
        if (!hasDigit) {
            throw new IllegalArgumentException("Password must contain at least one number");
        }
        if (!hasSpecial) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }

        User user = resetToken.getUser();

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}