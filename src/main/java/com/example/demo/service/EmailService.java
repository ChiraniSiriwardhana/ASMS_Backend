package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.url}")
    private String appUrl;

    public void sendEmployeeCreationEmail(String toEmail, String username, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to ASMS - Set Your Password");

            // Frontend link for setting password
            String setPasswordLink = "http://localhost:3000/set-password?token=" + token;

            String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                    "<h2 style='color: #333;'>Welcome to Automobile Management System!</h2>" +
                    "<p>Dear Employee,</p>" +
                    "<p>Your account has been created successfully.</p>" +
                    "<div style='background-color: #f5f5f5; padding: 15px; margin: 20px 0; border-left: 4px solid #007bff;'>" +
                    "<p><strong>Username:</strong> " + username + "</p>" +
                    "<p><strong>Your Token:</strong> <code style='background: #e9ecef; padding: 5px;'>" + token + "</code></p>" +
                    "</div>" +
                    "<h3>How to Set Your Password:</h3>" +
                    "<p>Click the button below to set your password:</p>" +
                    "<p style='text-align: center; margin: 30px 0;'>" +
                    "<a href='" + setPasswordLink + "' style='display: inline-block; padding: 12px 30px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;'>Set Your Password</a>" +
                    "</p>" +
                    "<p>Or copy and paste this link in your browser:</p>" +
                    "<p style='background: #f8f9fa; padding: 10px; word-break: break-all; border-radius: 5px;'>" +
                    "<a href='" + setPasswordLink + "' style='color: #007bff;'>" + setPasswordLink + "</a>" +
                    "</p>" +
                    "<p style='color: #dc3545; margin-top: 20px;'><strong>Important:</strong> This link will expire in 24 hours.</p>" +
                    "<p>After setting your password, you can login at <a href='http://localhost:3001/signin'>http://localhost:3001/signin</a> with your username: <strong>" + username + "</strong></p>" +
                    "<p style='margin-top: 30px;'>Best regards,<br/>ASMS Team</p>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            System.out.println("Email sent successfully to: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send email to: " + toEmail);
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    public void sendPasswordChangedEmail(String toEmail, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Changed Successfully");

            String htmlContent = "<html><body>" +
                    "<h2>Password Changed</h2>" +
                    "<p>Dear " + username + ",</p>" +
                    "<p>Your password has been changed successfully.</p>" +
                    "<p>If you did not make this change, please contact the administrator immediately.</p>" +
                    "<p>Best regards,<br/>ASMS Team</p>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send password change email to: " + toEmail);
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String toEmail, String username, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("ASMS - Password Reset Request");

            // Frontend link for resetting password
            String resetPasswordLink = "http://localhost:3000/reset-password?token=" + resetToken;

            String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                    "<h2 style='color: #333;'>Password Reset Request</h2>" +
                    "<p>Dear " + username + ",</p>" +
                    "<p>We received a request to reset your password for your ASMS account.</p>" +
                    "<p>Click the button below to reset your password:</p>" +
                    "<p style='text-align: center; margin: 30px 0;'>" +
                    "<a href='" + resetPasswordLink + "' style='display: inline-block; padding: 12px 30px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;'>Reset Password</a>" +
                    "</p>" +
                    "<p>Or copy and paste this link in your browser:</p>" +
                    "<p style='background: #f8f9fa; padding: 10px; word-break: break-all; border-radius: 5px;'>" +
                    "<a href='" + resetPasswordLink + "' style='color: #007bff;'>" + resetPasswordLink + "</a>" +
                    "</p>" +
                    "<p style='color: #dc3545; margin-top: 20px;'><strong>Important:</strong> This link will expire in 24 hours.</p>" +
                    "<p style='color: #6c757d;'>If you didn't request a password reset, please ignore this email or contact support if you have concerns.</p>" +
                    "<p style='margin-top: 30px;'>Best regards,<br/>ASMS Team</p>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            System.out.println("Password reset email sent successfully to: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send password reset email to: " + toEmail);
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    public void sendPasswordResetSuccessEmail(String toEmail, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("ASMS - Password Reset Successful");

            String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                    "<h2 style='color: #28a745;'>Password Reset Successful</h2>" +
                    "<p>Dear " + username + ",</p>" +
                    "<p>Your password has been reset successfully.</p>" +
                    "<p>You can now sign in to your ASMS account with your new password.</p>" +
                    "<p style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:3000/signin' style='display: inline-block; padding: 12px 30px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;'>Go to Sign In</a>" +
                    "</p>" +
                    "<p style='color: #dc3545;'><strong>Security Notice:</strong> If you didn't make this change, please contact support immediately.</p>" +
                    "<p style='margin-top: 30px;'>Best regards,<br/>ASMS Team</p>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            System.out.println("Password reset success email sent to: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send password reset success email to: " + toEmail);
            e.printStackTrace();
        }
    }
}


