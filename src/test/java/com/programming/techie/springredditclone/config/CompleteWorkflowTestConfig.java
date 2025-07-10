package com.programming.techie.springredditclone.config;

import com.programming.techie.springredditclone.model.NotificationEmail;
import com.programming.techie.springredditclone.service.MailService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * Test configuration for complete workflow integration tests.
 * Captures email notifications to verify they are sent correctly.
 */
@TestConfiguration
public class CompleteWorkflowTestConfig {

    private final List<String> capturedEmails = new ArrayList<>();

    @Bean
    @Primary
    public MailService testMailService() {
        return new MailService() {
            @Override
            public void sendMail(NotificationEmail notificationEmail) {
                capturedEmails.add("To: " + notificationEmail.getRecipient() + 
                                 ", Subject: " + notificationEmail.getSubject() + 
                                 ", Body: " + notificationEmail.getBody());
                // Don't actually send emails during tests
            }
        };
    }

    public List<String> getCapturedEmails() {
        return new ArrayList<>(capturedEmails);
    }

    public void clearCapturedEmails() {
        capturedEmails.clear();
    }

    /**
     * Get the last captured email
     */
    public NotificationEmail getLastCapturedEmail() {
        if (capturedEmails.isEmpty()) {
            return null;
        }
        String lastEmail = capturedEmails.get(capturedEmails.size() - 1);
        // Parse the captured email string back to NotificationEmail object
        // This is a simplified implementation for testing
        String[] parts = lastEmail.split(", ");
        String recipient = parts[0].replace("To: ", "");
        String subject = parts[1].replace("Subject: ", "");
        String body = parts[2].replace("Body: ", "");
        return new NotificationEmail(subject, recipient, body);
    }

    /**
     * Check if verification email was sent for a specific user
     */
    public boolean wasVerificationEmailSent(String email) {
        return capturedEmails.stream()
                .anyMatch(e -> e.contains("To: " + email) && 
                               e.contains("Subject: Please Activate your Account"));
    }
} 