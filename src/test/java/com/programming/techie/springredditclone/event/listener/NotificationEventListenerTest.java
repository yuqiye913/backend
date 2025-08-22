package com.programming.techie.springredditclone.event.listener;

import com.programming.techie.springredditclone.event.PostCommentedEvent;
import com.programming.techie.springredditclone.event.PostLikedEvent;
import com.programming.techie.springredditclone.event.UserFollowedEvent;
import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationEventListener eventListener;

    private User actor;
    private User recipient;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        actor = new User();
        actor.setUserId(1L);
        actor.setUsername("actor");
        actor.setEmail("actor@test.com");
        actor.setEnabled(true);

        recipient = new User();
        recipient.setUserId(2L);
        recipient.setUsername("recipient");
        recipient.setEmail("recipient@test.com");
        recipient.setEnabled(true);

        post = new Post();
        post.setPostId(1L);
        post.setPostName("Test Post");
        post.setDescription("Test Description");
        post.setUser(recipient);
        post.setCreatedDate(Instant.now());
        post.setVoteCount(0);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setUser(actor);
        comment.setPost(post);
        comment.setCreatedDate(Instant.now());
    }

    @Test
    void handlePostLikedEvent_ShouldCreateLikeNotification() {
        // Arrange
        PostLikedEvent event = new PostLikedEvent(this, actor, recipient, post);

        // Act
        eventListener.handlePostLikedEvent(event);

        // Assert
        verify(notificationService, times(1))
                .createLikeNotification(eq(actor), eq(recipient), eq(1L));
    }

    @Test
    void handlePostLikedEvent_ShouldNotCreateNotification_WhenUserLikesOwnPost() {
        // Arrange
        PostLikedEvent event = new PostLikedEvent(this, recipient, recipient, post);

        // Act
        eventListener.handlePostLikedEvent(event);

        // Assert
        verify(notificationService, never()).createLikeNotification(any(), any(), any());
    }

    @Test
    void handlePostCommentedEvent_ShouldCreateCommentNotification() {
        // Arrange
        PostCommentedEvent event = new PostCommentedEvent(this, actor, recipient, post, comment);

        // Act
        eventListener.handlePostCommentedEvent(event);

        // Assert
        verify(notificationService, times(1))
                .createCommentNotification(eq(actor), eq(recipient), eq(1L), eq(1L));
    }

    @Test
    void handlePostCommentedEvent_ShouldNotCreateNotification_WhenUserCommentsOnOwnPost() {
        // Arrange
        PostCommentedEvent event = new PostCommentedEvent(this, recipient, recipient, post, comment);

        // Act
        eventListener.handlePostCommentedEvent(event);

        // Assert
        verify(notificationService, never()).createCommentNotification(any(), any(), any(), any());
    }

    @Test
    void handleUserFollowedEvent_ShouldCreateFollowNotification() {
        // Arrange
        UserFollowedEvent event = new UserFollowedEvent(this, actor, recipient);

        // Act
        eventListener.handleUserFollowedEvent(event);

        // Assert
        verify(notificationService, times(1))
                .createFollowNotification(eq(actor), eq(recipient));
    }

    @Test
    void handleUserFollowedEvent_ShouldNotCreateNotification_WhenUserFollowsSelf() {
        // Arrange
        UserFollowedEvent event = new UserFollowedEvent(this, recipient, recipient);

        // Act
        eventListener.handleUserFollowedEvent(event);

        // Assert
        verify(notificationService, never()).createFollowNotification(any(), any());
    }

    @Test
    void handlePostLikedEvent_ShouldHandleExceptionGracefully() {
        // Arrange
        PostLikedEvent event = new PostLikedEvent(this, actor, recipient, post);
        doThrow(new RuntimeException("Notification service error"))
                .when(notificationService).createLikeNotification(any(), any(), any());

        // Act & Assert - should not throw exception
        eventListener.handlePostLikedEvent(event);

        // Verify the method was still called
        verify(notificationService, times(1))
                .createLikeNotification(eq(actor), eq(recipient), eq(1L));
    }

    @Test
    void handlePostCommentedEvent_ShouldHandleExceptionGracefully() {
        // Arrange
        PostCommentedEvent event = new PostCommentedEvent(this, actor, recipient, post, comment);
        doThrow(new RuntimeException("Notification service error"))
                .when(notificationService).createCommentNotification(any(), any(), any(), any());

        // Act & Assert - should not throw exception
        eventListener.handlePostCommentedEvent(event);

        // Verify the method was still called
        verify(notificationService, times(1))
                .createCommentNotification(eq(actor), eq(recipient), eq(1L), eq(1L));
    }

    @Test
    void handleUserFollowedEvent_ShouldHandleExceptionGracefully() {
        // Arrange
        UserFollowedEvent event = new UserFollowedEvent(this, actor, recipient);
        doThrow(new RuntimeException("Notification service error"))
                .when(notificationService).createFollowNotification(any(), any());

        // Act & Assert - should not throw exception
        eventListener.handleUserFollowedEvent(event);

        // Verify the method was still called
        verify(notificationService, times(1))
                .createFollowNotification(eq(actor), eq(recipient));
    }
} 