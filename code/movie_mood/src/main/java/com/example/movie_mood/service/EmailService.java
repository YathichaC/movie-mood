package com.example.movie_mood.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:moviemood8080@gmail.com}")
    private String mailUsername = "moviemood8080@gmail.com";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendResetPasswordEmail(String toEmail, String emailContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(mailUsername);
            helper.setTo(toEmail);
            helper.setSubject("Reset Your Password — MOVIEMOOD");
            helper.setText(emailContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Failed to send reset email to " + toEmail + ": " + e.getMessage());
        }
    }
}
