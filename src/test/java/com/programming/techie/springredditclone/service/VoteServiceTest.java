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
import com.programming.techie.springredditclone.service.BlockService;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.atLeastOnce;

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

    @Mock
    private BlockService blockService;

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
        
        // Setup BlockService mocks to allow voting by default
        lenient().when(blockService.isBlockedByUser(any(Long.class))).thenReturn(false);
        lenient().when(blockService.hasBlockedUser(any(Long.class))).thenReturn(false);
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
        verify(postRepository, atLeastOnce()).save(any(Post.class));
        verify(eventPublisher).publishEvent(eventCaptor.capture());
    }

    @Test
    @DisplayName("Should cancel upvote when voting same type again")
    void shouldCancelUpvoteWhenVotingSameTypeAgain() {
        // Given
        testPost.setVoteCount(1); // Already upvoted
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.vote(upvoteDto);

        // Then
        verify(voteRepository).delete(existingVote);
        verify(postRepository, atLeastOnce()).save(any(Post.class));
        verify(eventPublisher, never()).publishEvent(any(PostLikedEvent.class));
    }

    @Test
    @DisplayName("Should allow re-upvote after cancellation")
    void shouldAllowReUpvoteAfterCancellation() {
        // Given: first upvote
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
        voteService.vote(upvoteDto);
        // Cancel upvote
        testPost.setVoteCount(1);
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));
        voteService.vote(upvoteDto);
        // Re-upvote
        testPost.setVoteCount(0);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());
        voteService.vote(upvoteDto);
        // Then
        verify(voteRepository, atLeast(2)).save(any(Vote.class));
        verify(voteRepository).delete(existingVote);
        verify(postRepository, atLeast(3)).save(any(Post.class));
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
        verify(postRepository, atLeastOnce()).save(any(Post.class));
        verify(eventPublisher, never()).publishEvent(any(PostLikedEvent.class));
    }

    @Test
    @DisplayName("Should cancel downvote when voting same type again")
    void shouldCancelDownvoteWhenVotingSameTypeAgain() {
        // Given
        testPost.setVoteCount(-1); // Already downvoted
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.DOWNVOTE)
                .post(testPost)
                .user(testUser)
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.vote(downvoteDto);

        // Then
        verify(voteRepository).delete(existingVote);
        verify(postRepository, atLeastOnce()).save(any(Post.class));
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
        verify(postRepository, atLeastOnce()).save(any(Post.class));
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
        verify(postRepository, atLeastOnce()).save(any(Post.class));
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
        verify(commentRepository).save(any(Comment.class));
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
        verify(commentRepository).save(any(Comment.class));
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
    @DisplayName("Should cancel comment upvote when voting same type again")
    void shouldCancelCommentUpvoteWhenVotingSameTypeAgain() {
        // Given
        testComment.setVoteCount(1); // Start with 1 vote count (from existing upvote)
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .comment(testComment)
                .user(testUser)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.of(existingVote));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.voteOnComment(commentUpvoteRequest);

        // Then
        verify(voteRepository).delete(existingVote);
        verify(commentRepository).save(any(Comment.class)); // Comment should be saved after vote cancellation
    }

    @Test
    @DisplayName("Should cancel comment downvote when voting same type again")
    void shouldCancelCommentDownvoteWhenVotingSameTypeAgain() {
        // Given
        testComment.setVoteCount(-1); // Start with -1 vote count (from existing downvote)
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.DOWNVOTE)
                .comment(testComment)
                .user(testUser)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByCommentAndUser(testComment, testUser)).thenReturn(Optional.of(existingVote));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.voteOnComment(commentDownvoteRequest);

        // Then
        verify(voteRepository).delete(existingVote);
        verify(commentRepository).save(any(Comment.class)); // Comment should be saved after vote cancellation
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

    @Test
    @DisplayName("Should handle vote cancellation with multiple users")
    void shouldHandleVoteCancellationWithMultipleUsers() {
        // Given - User 1 upvotes
        testPost.setVoteCount(0);
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

        // When - User 1 upvotes
        voteService.vote(upvoteDto);

        // Then - Verify vote count is 1
        verify(postRepository, atLeastOnce()).save(any(Post.class));

        // Given - User 1 cancels vote
        testPost.setVoteCount(1);
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));

        // When - User 1 cancels vote
        voteService.vote(upvoteDto);

        // Then - Verify vote count is 0
        verify(postRepository, atLeastOnce()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should not publish event when cancelling upvote")
    void shouldNotPublishEventWhenCancellingUpvote() {
        // Given - User has existing upvote
        testPost.setVoteCount(1);
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - User cancels upvote
        voteService.vote(upvoteDto);

        // Then - Verify no event is published for cancellation
        verify(eventPublisher, never()).publishEvent(any(PostLikedEvent.class));
        verify(voteRepository).delete(existingVote);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should actually decrease vote count when cancelling upvote")
    void shouldActuallyDecreaseVoteCountWhenCancellingUpvote() {
        // Given
        testPost.setVoteCount(1); // Start with 1 vote count (from existing upvote)
        Vote existingVote = Vote.builder()
                .voteId(1L)
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.of(existingVote));
        
        // Capture the saved post to verify vote count
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.vote(upvoteDto);

        // Then
        verify(voteRepository).delete(existingVote);
        verify(postRepository).save(any(Post.class));
        
        // Verify the actual vote count was decreased
        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getVoteCount()).isEqualTo(0); // Should be 0 after cancellation
    }

    @Test
    @DisplayName("Should actually increase vote count when creating new upvote")
    void shouldActuallyIncreaseVoteCountWhenCreatingNewUpvote() {
        // Given
        testPost.setVoteCount(0); // Start with 0 vote count
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Vote expectedVote = Vote.builder()
                .voteType(VoteType.UPVOTE)
                .post(testPost)
                .user(testUser)
                .build();
        when(voteMapper.mapToVote(upvoteDto, testPost, testUser)).thenReturn(expectedVote);
        
        // Capture the saved post to verify vote count
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        voteService.vote(upvoteDto);

        // Then
        verify(voteRepository).save(any(Vote.class));
        verify(postRepository).save(any(Post.class));
        
        // Verify the actual vote count was increased
        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getVoteCount()).isEqualTo(1); // Should be 1 after upvote
    }
} 