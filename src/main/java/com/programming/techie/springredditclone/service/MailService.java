package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.model.NotificationEmail;

public interface MailService {
    
    /**
     * Send email notification
     * @param notificationEmail Email to send
     */
    void sendMail(NotificationEmail notificationEmail);
}