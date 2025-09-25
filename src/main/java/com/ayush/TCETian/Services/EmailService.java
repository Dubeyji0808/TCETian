package com.ayush.TCETian.Services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) throws Exception {
        String subject = "Please Verify Your Email";
        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
        String body = "<p>Thank you for registering. Please click the link below to verify your email:</p>" +
                "<a href=\"" + verificationUrl + "\">Verify Email</a>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true); // true indicates HTML

        mailSender.send(message);
    }
}

