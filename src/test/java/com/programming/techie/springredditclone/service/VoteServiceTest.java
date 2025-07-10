package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.CommentVoteRequest;
import com.programming.techie.springredditclone.dto.VoteDto;
import com.programming.techie.springredditclone.exceptions.PostNotFoundException;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.Vote;
import com.programming.techie.springredditclone.model.VoteType;
import com.programming.techie.springredditclone.repository.CommentRepository;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.VoteRepository;
import com.programming.techie.springredditclone.event.PostLikedEvent;
import com.programming.techie.springredditclone.service.impl.VoteServiceImpl;
import com.programming.techie.springredditclone.mapper.VoteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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
    private CommentRepository commentRepository;

    @Mock
    private AuthService authService;

    @Mock
    private VoteMapper voteMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<PostLikedEvent> eventCaptor;

    @InjectMocks
    private VoteServiceImpl voteService;

    private User testUser;
    private User postOwner;
    private Post testPost;
    private Comment testComment;
    private VoteDto upvoteDto;
    private VoteDto downvoteDto;
    private CommentVoteRequest commentUpvoteRequest;
    private CommentVoteRequest commentDownvoteRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);

        postOwner = new User();
        postOwner.setUserId(2L);
        postOwner.setUsername("postowner");
        postOwner.setEmail("owner@example.com");
        postOwner.setEnabled(true);

        testPost = new Post();
        testPost.setPostId(1L);
        testPost.setPostName("Test Post");
        testPost.setDescription("Test Description");
        testPost.setVoteCount(0);
        testPost.setUser(postOwner); // Different user owns the post
        testPost.setCreatedDate(Instant.now());

        upvoteDto = new VoteDto();
        upvoteDto.setPostId(1L);
        upvoteDto.setVoteType(VoteType.UPVOTE);

        downvoteDto = new VoteDto();
        downvoteDto.setPostId(1L);
        downvoteDto.setVoteType(VoteType.DOWNVOTE);

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setText("Test comment");
        testComment.setPost(testPost);
        testComment.setUser(testUser);
        testComment.setCreatedDate(Instant.now());
        testComment.setVoteCount(0);
        testComment.setReplyCount(0);

        commentUpvoteRequest = CommentVoteRequest.builder()
                .commentId(1L)
                .voteType(VoteType.UPVOTE)
                .build();

        commentDownvoteRequest = CommentVoteRequest.builder()
                .commentId(1L)
                .voteType(VoteType.DOWNVOTE)
                .build();
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
        
        Vote expectedVote = Vote.builder()
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();
        when(voteMapper.mapToVote(upvoteDto, testPost, testUser)).thenReturn(expectedVote);

        // When
        voteService.vote(upvoteDto);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(postRepository).save(argThat(post -> post.getVoteCount() == 1));
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        PostLikedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getActor()).isEqualTo(testUser);
        assertThat(capturedEvent.getRecipient()).isEqualTo(postOwner); // Post owner should be recipient
        assertThat(capturedEvent.getPost()).isEqualTo(testPost);
        assertThat(capturedEvent.getPostId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should publish event when user likes someone else's post")
    void shouldPublishEventWhenLikingOthersPost() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Vote expectedVote = Vote.builder()
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();
        when(voteMapper.mapToVote(upvoteDto, testPost, testUser)).thenReturn(expectedVote);

        // When
        voteService.vote(upvoteDto);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(postRepository).save(argThat(post -> post.getVoteCount() == 1));
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        PostLikedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getActor()).isEqualTo(testUser);
        assertThat(capturedEvent.getRecipient()).isEqualTo(postOwner);
        assertThat(capturedEvent.getPost()).isEqualTo(testPost);
        assertThat(capturedEvent.getPostId()).isEqualTo(1L);
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
        
        Vote expectedVote = Vote.builder()
                .voteType(VoteType.DOWNVOTE)
                .post(testPost)
                .user(testUser)
                .build();
        when(voteMapper.mapToVote(downvoteDto, testPost, testUser)).thenReturn(expectedVote);

        // When
        voteService.vote(downvoteDto);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(postRepository).save(argThat(post -> post.getVoteCount() == -1));
        // Downvotes should not publish events
        verify(eventPublisher, never()).publishEvent(any(PostLikedEvent.class));
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

    // Comment Voting Tests
    @Test
    @DisplayName("Should create new upvote on comment when user hasn't voted before")
    void shouldCreateNewCommentUpvote() {
        // Given
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.empty());
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.voteOnComment(commentUpvoteRequest);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(commentRepository).save(argThat(comment -> comment.getVoteCount() == 1));
    }

    @Test
    @DisplayName("Should create new downvote on comment when user hasn't voted before")
    void shouldCreateNewCommentDownvote() {
        // Given
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.empty());
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.voteOnComment(commentDownvoteRequest);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(commentRepository).save(argThat(comment -> comment.getVoteCount() == -1));
    }

    @Test
    @DisplayName("Should update existing comment vote from upvote to downvote")
    void shouldUpdateCommentVoteFromUpvoteToDownvote() {
        // Given
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .comment(testComment)
                .user(testUser)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.of(existingVote));
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.voteOnComment(commentDownvoteRequest);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to vote same type on comment again")
    void shouldThrowExceptionForSameCommentVoteType() {
        // Given
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .comment(testComment)
                .user(testUser)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.of(existingVote));

        // When & Then
        assertThatThrownBy(() -> voteService.voteOnComment(commentUpvoteRequest))
                .isInstanceOf(SpringRedditException.class)
                .hasMessageContaining("You have already UPVOTE'd for this comment");

        verify(voteRepository, never()).save(any(Vote.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw exception when comment not found")
    void shouldThrowExceptionWhenCommentNotFound() {
        // Given
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voteService.voteOnComment(commentUpvoteRequest))
                .isInstanceOf(SpringRedditException.class)
                .hasMessageContaining("Comment Not Found with ID - 1");

        verify(voteRepository, never()).save(any(Vote.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should get comment vote count")
    void shouldGetCommentVoteCount() {
        // Given
        testComment.setVoteCount(5);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        // When
        Integer voteCount = voteService.getCommentVoteCount(1L);

        // Then
        assertThat(voteCount).isEqualTo(5);
    }

    @Test
    @DisplayName("Should throw exception when getting vote count for non-existent comment")
    void shouldThrowExceptionWhenGettingVoteCountForNonExistentComment() {
        // Given
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voteService.getCommentVoteCount(1L))
                .isInstanceOf(SpringRedditException.class)
                .hasMessageContaining("Comment Not Found with ID - 1");
    }
} 