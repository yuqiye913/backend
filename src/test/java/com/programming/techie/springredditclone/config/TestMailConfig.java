package com.programming.techie.springredditclone.config;

import com.programming.techie.springredditclone.model.NotificationEmail;
import com.programming.techie.springredditclone.service.MailService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * Test configuration for email service that captures emails without sending them
 * This allows verification of email content without external dependencies
 */
@TestConfiguration
public class TestMailConfig {

    private final List<NotificationEmail> sentEmails = new ArrayList<>();

    @Bean
    @Primary
    public MailService testMailService() {
        return new MailService() {
            @Override
            public void sendMail(NotificationEmail notificationEmail) {
                // Capture email for verification instead of sending
                sentEmails.add(notificationEmail);
                System.out.println("TEST EMAIL CAPTURED: " + notificationEmail.getSubject() + " to " + notificationEmail.getRecipient());
            }
        };
    }

    /**
     * Get all captured emails for verification
     */
    public List<NotificationEmail> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }

    /**
     * Clear captured emails
     */
    public void clearSentEmails() {
        sentEmails.clear();
    }

    /**
     * Get the last sent email
     */
    public NotificationEmail getLastSentEmail() {
        return sentEmails.isEmpty() ? null : sentEmails.get(sentEmails.size() - 1);
    }
} 