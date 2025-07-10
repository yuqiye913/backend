package com.programming.techie.springredditclone.event.listener;

import com.programming.techie.springredditclone.event.PostCommentedEvent;
import com.programming.techie.springredditclone.event.PostLikedEvent;
import com.programming.techie.springredditclone.event.UserFollowedEvent;
import com.programming.techie.springredditclone.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener for user actions that trigger notifications
 * Uses @Async to handle notifications asynchronously
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    /**
     * Handle post liked events
     */
    @EventListener
    @Async
    public void handlePostLikedEvent(PostLikedEvent event) {
        try {
            log.info("Processing post liked event for post {} by user {}", 
                    event.getPostId(), event.getActor().getUsername());
            
            // Don't create notification if user likes their own post
            if (event.getActor().equals(event.getRecipient())) {
                log.debug("Skipping notification - user liked their own post");
                return;
            }
            
            notificationService.createLikeNotification(
                    event.getActor(),      // liker
                    event.getRecipient(),  // post owner
                    event.getPostId()      // post id
            );
            
            log.info("Successfully created like notification for post {}", event.getPostId());
        } catch (Exception e) {
            log.error("Error processing post liked event for post {}: {}", 
                    event.getPostId(), e.getMessage(), e);
        }
    }

    /**
     * Handle post commented events
     */
    @EventListener
    @Async
    public void handlePostCommentedEvent(PostCommentedEvent event) {
        try {
            log.info("Processing post commented event for post {} by user {}", 
                    event.getPostId(), event.getActor().getUsername());
            
            // Don't create notification if user comments on their own post
            if (event.getActor().equals(event.getRecipient())) {
                log.debug("Skipping notification - user commented on their own post");
                return;
            }
            
            notificationService.createCommentNotification(
                    event.getActor(),      // commenter
                    event.getRecipient(),  // post owner
                    event.getPostId(),     // post id
                    event.getCommentId()   // comment id
            );
            
            log.info("Successfully created comment notification for post {}", event.getPostId());
        } catch (Exception e) {
            log.error("Error processing post commented event for post {}: {}", 
                    event.getPostId(), e.getMessage(), e);
        }
    }

    /**
     * Handle user followed events
     */
    @EventListener
    @Async
    public void handleUserFollowedEvent(UserFollowedEvent event) {
        try {
            log.info("Processing user followed event: {} followed {}", 
                    event.getActor().getUsername(), event.getRecipient().getUsername());
            
            // Don't create notification if user follows themselves
            if (event.getActor().equals(event.getRecipient())) {
                log.debug("Skipping notification - user followed themselves");
                return;
            }
            
            notificationService.createFollowNotification(
                    event.getActor(),      // follower
                    event.getRecipient()   // followed
            );
            
            log.info("Successfully created follow notification for user {}", 
                    event.getRecipient().getUsername());
        } catch (Exception e) {
            log.error("Error processing user followed event: {}", e.getMessage(), e);
        }
    }
} 