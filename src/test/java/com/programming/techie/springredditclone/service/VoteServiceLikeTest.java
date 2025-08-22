package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.Vote;
import com.programming.techie.springredditclone.model.VoteType;
import com.programming.techie.springredditclone.repository.CommentRepository;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.VoteRepository;
import com.programming.techie.springredditclone.service.impl.VoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceLikeTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AuthService authService;

    @Mock
    private BlockService blockService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private VoteServiceImpl voteService;

    private User testUser;
    private Post testPost;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");

        testPost = new Post();
        testPost.setPostId(1L);
        testPost.setUser(testUser);
        testPost.setVoteCount(0);

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setUser(testUser);
        testComment.setVoteCount(0);
    }

    @Test
    @DisplayName("Should do nothing when trying to like already liked post (idempotent)")
    void shouldDoNothingWhenTryingToLikeAlreadyLikedPost() {
        // Given
        Vote existingVote = Vote.builder()
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(blockService.isBlockedByUser(anyLong())).thenReturn(false);
        when(blockService.hasBlockedUser(anyLong())).thenReturn(false);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));

        // When
        voteService.likePost(1L);

        // Then - should do nothing, no exceptions thrown
        verify(voteRepository, never()).save(any(Vote.class));
        verify(postRepository, never()).save(any(Post.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should do nothing when trying to unlike not liked post (idempotent)")
    void shouldDoNothingWhenTryingToUnlikeNotLikedPost() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());

        // When
        voteService.unlikePost(1L);

        // Then - should do nothing, no exceptions thrown
        verify(voteRepository, never()).delete(any(Vote.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should do nothing when trying to like already liked comment (idempotent)")
    void shouldDoNothingWhenTryingToLikeAlreadyLikedComment() {
        // Given
        Vote existingVote = Vote.builder()
                .voteType(VoteType.UPVOTE)
                .comment(testComment)
                .user(testUser)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(blockService.isBlockedByUser(anyLong())).thenReturn(false);
        when(blockService.hasBlockedUser(anyLong())).thenReturn(false);
        when(voteRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.of(existingVote));

        // When
        voteService.likeComment(1L);

        // Then - should do nothing, no exceptions thrown
        verify(voteRepository, never()).save(any(Vote.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should do nothing when trying to unlike not liked comment (idempotent)")
    void shouldDoNothingWhenTryingToUnlikeNotLikedComment() {
        // Given
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.empty());

        // When
        voteService.unlikeComment(1L);

        // Then - should do nothing, no exceptions thrown
        verify(voteRepository, never()).delete(any(Vote.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should successfully like post when not liked before")
    void shouldSuccessfullyLikePostWhenNotLikedBefore() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(blockService.isBlockedByUser(anyLong())).thenReturn(false);
        when(blockService.hasBlockedUser(anyLong())).thenReturn(false);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.likePost(1L);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(postRepository).save(any(Post.class));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    @DisplayName("Should successfully unlike post when liked before")
    void shouldSuccessfullyUnlikePostWhenLikedBefore() {
        // Given
        Vote existingVote = Vote.builder()
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.unlikePost(1L);

        // Then
        verify(voteRepository).delete(existingVote);
        verify(postRepository).save(any(Post.class));
    }
} 