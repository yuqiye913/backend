package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.VoteDto;
import com.programming.techie.springredditclone.exceptions.PostNotFoundException;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.Vote;
import com.programming.techie.springredditclone.model.VoteType;
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

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private VoteServiceImpl voteService;

    private User testUser;
    private Post testPost;
    private VoteDto upvoteDto;
    private VoteDto downvoteDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);

        testPost = Post.builder()
                .postId(1L)
                .postName("Test Post")
                .description("Test Description")
                .voteCount(0)
                .user(testUser)
                .createdDate(Instant.now())
                .build();

        upvoteDto = new VoteDto();
        upvoteDto.setPostId(1L);
        upvoteDto.setVoteType(VoteType.UPVOTE);

        downvoteDto = new VoteDto();
        downvoteDto.setPostId(1L);
        downvoteDto.setVoteType(VoteType.DOWNVOTE);
    }

    @Test
    @DisplayName("Should create new upvote when user hasn't voted before")
    void shouldCreateNewUpvote() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.vote(upvoteDto);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(postRepository).save(argThat(post -> post.getVoteCount() == 1));
    }

    @Test
    @DisplayName("Should create new downvote when user hasn't voted before")
    void shouldCreateNewDownvote() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.vote(downvoteDto);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(postRepository).save(argThat(post -> post.getVoteCount() == -1));
    }

    @Test
    @DisplayName("Should update existing vote from upvote to downvote")
    void shouldUpdateVoteFromUpvoteToDownvote() {
        // Given
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.vote(downvoteDto);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should update existing vote from downvote to upvote")
    void shouldUpdateVoteFromDownvoteToUpvote() {
        // Given
        testPost.setVoteCount(-1); // Start with -1 vote count
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.DOWNVOTE)
                .post(testPost)
                .user(testUser)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.vote(upvoteDto);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to vote same type again")
    void shouldThrowExceptionForSameVoteType() {
        // Given
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));

        // When & Then
        assertThatThrownBy(() -> voteService.vote(upvoteDto))
                .isInstanceOf(SpringRedditException.class)
                .hasMessageContaining("You have already UPVOTE'd for this post");

        verify(voteRepository, never()).save(any(Vote.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw exception when post not found")
    void shouldThrowExceptionWhenPostNotFound() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voteService.vote(upvoteDto))
                .isInstanceOf(PostNotFoundException.class);

        verify(voteRepository, never()).save(any(Vote.class));
        verify(postRepository, never()).save(any(Post.class));
    }
} 