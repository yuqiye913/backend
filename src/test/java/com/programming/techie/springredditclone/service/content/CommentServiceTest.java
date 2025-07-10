package com.programming.techie.springredditclone.service.content;

import com.programming.techie.springredditclone.dto.CommentsDto;
import com.programming.techie.springredditclone.dto.CursorPageResponse;
import com.programming.techie.springredditclone.dto.CreateCommentRequest;
import com.programming.techie.springredditclone.exceptions.PostNotFoundException;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.mapper.CommentMapper;
import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.NotificationEmail;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.CommentRepository;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.CommentService;
import com.programming.techie.springredditclone.service.impl.CommentServiceImpl;
import com.programming.techie.springredditclone.service.impl.MailContentBuilder;
import com.programming.techie.springredditclone.service.MailService;
import com.programming.techie.springredditclone.util.CursorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MailContentBuilder mailContentBuilder;

    @Mock
    private MailService mailService;

    @Mock
    private CursorUtil cursorUtil;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private CommentsDto testCommentDto;
    private CreateCommentRequest testCreateCommentRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testPost = new Post();
        testPost.setPostId(1L);
        testPost.setPostName("Test Post");
        testPost.setDescription("Test Description");
        testPost.setUser(testUser);

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setText("Test comment");
        testComment.setPost(testPost);
        testComment.setUser(testUser);
        testComment.setCreatedDate(Instant.now());
        testComment.setVoteCount(0);
        testComment.setReplyCount(0);

        testCommentDto = CommentsDto.builder()
                .id(1L)
                .postId(1L)
                .text("Test comment")
                .userName("testuser")
                .createdDate(Instant.now())
                .voteCount(0)
                .replyCount(0)
                .build();

        testCreateCommentRequest = CreateCommentRequest.builder()
                .postId(1L)
                .text("Test comment")
                .build();
    }

    @Test
    void save_ValidComment_ShouldSaveSuccessfully() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(commentMapper.map(any(CommentsDto.class), any(Post.class), any(User.class)))
                .thenReturn(testComment);
        when(mailContentBuilder.build(anyString())).thenReturn("Email content");

        // Act
        commentService.save(testCommentDto);

        // Assert
        verify(commentRepository).save(testComment);
        verify(mailService).sendMail(any());
    }

    @Test
    void save_InvalidPostId_ShouldThrowException() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PostNotFoundException.class, () -> {
            commentService.save(CommentsDto.builder().postId(999L).text("Test").build());
        });
    }

    @Test
    void getAllCommentsForPost_ValidPostId_ShouldReturnComments() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.findByPost(testPost)).thenReturn(Arrays.asList(testComment));
        when(commentMapper.mapToDto(testComment)).thenReturn(testCommentDto);

        // Act
        List<CommentsDto> result = commentService.getAllCommentsForPost(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCommentDto, result.get(0));
    }

    @Test
    void getAllCommentsForPost_InvalidPostId_ShouldThrowException() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PostNotFoundException.class, () -> {
            commentService.getAllCommentsForPost(999L);
        });
    }

    @Test
    void getAllCommentsForUser_ValidUsername_ShouldReturnComments() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(commentRepository.findAllByUser(testUser)).thenReturn(Arrays.asList(testComment));
        when(commentMapper.mapToDto(testComment)).thenReturn(testCommentDto);

        // Act
        List<CommentsDto> result = commentService.getAllCommentsForUser("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCommentDto, result.get(0));
    }

    @Test
    void getAllCommentsForUser_InvalidUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            commentService.getAllCommentsForUser("nonexistent");
        });
    }

    @Test
    void getCommentsForPost_FirstPage_ShouldReturnPaginatedComments() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.findTopLevelCommentsByPostFirstPage(1L)).thenReturn(comments);
        when(commentMapper.mapToDto(testComment)).thenReturn(testCommentDto);

        // Act
        CursorPageResponse<CommentsDto> result = commentService.getCommentsForPost(1L, null, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCommentDto, result.getContent().get(0));
        assertFalse(result.isHasMore());
        assertEquals(20, result.getLimit());
    }

    @Test
    void getCommentsForPost_WithCursor_ShouldReturnPaginatedComments() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);
        CursorUtil.CursorData cursorData = new CursorUtil.CursorData(Instant.now(), 1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(cursorUtil.decodeCursor("test-cursor")).thenReturn(cursorData);
        when(commentRepository.findTopLevelCommentsByPostWithCursor(1L, 1L)).thenReturn(comments);
        when(commentMapper.mapToDto(testComment)).thenReturn(testCommentDto);

        // Act
        CursorPageResponse<CommentsDto> result = commentService.getCommentsForPost(1L, "test-cursor", 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCommentDto, result.getContent().get(0));
    }

    @Test
    void getCommentsForPost_WithLimitExceedingMax_ShouldUseMaxLimit() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.findTopLevelCommentsByPostFirstPage(1L)).thenReturn(comments);
        when(commentMapper.mapToDto(testComment)).thenReturn(testCommentDto);

        // Act
        CursorPageResponse<CommentsDto> result = commentService.getCommentsForPost(1L, null, 100);

        // Assert
        assertEquals(50, result.getLimit()); // Should be capped at 50
    }

    @Test
    void getCommentsForUser_FirstPage_ShouldReturnPaginatedComments() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(commentRepository.findCommentsByUserFirstPage("testuser")).thenReturn(comments);
        when(commentMapper.mapToDto(testComment)).thenReturn(testCommentDto);

        // Act
        CursorPageResponse<CommentsDto> result = commentService.getCommentsForUser("testuser", null, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCommentDto, result.getContent().get(0));
    }

    @Test
    void getRepliesForComment_FirstPage_ShouldReturnPaginatedReplies() {
        // Arrange
        List<Comment> replies = Arrays.asList(testComment);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.findRepliesByCommentFirstPage(1L)).thenReturn(replies);
        when(commentMapper.mapToDto(testComment)).thenReturn(testCommentDto);

        // Act
        CursorPageResponse<CommentsDto> result = commentService.getRepliesForComment(1L, null, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCommentDto, result.getContent().get(0));
    }

    @Test
    void getRepliesForComment_InvalidCommentId_ShouldThrowException() {
        // Arrange
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> {
            commentService.getRepliesForComment(999L, null, 20);
        });
    }

    @Test
    void containsSwearWords_WithSwearWord_ShouldThrowException() {
        // Act & Assert
        assertThrows(SpringRedditException.class, () -> {
            commentService.containsSwearWords("This comment contains shit");
        });
    }

    @Test
    void containsSwearWords_WithoutSwearWord_ShouldReturnFalse() {
        // Act
        boolean result = commentService.containsSwearWords("This is a clean comment");

        // Assert
        assertFalse(result);
    }

    @Test
    void getCommentsForPost_WithNextCursor_ShouldIncludeNextCursor() {
        // Arrange
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setCreatedDate(Instant.now());
        
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setCreatedDate(Instant.now());
        
        List<Comment> comments = Arrays.asList(comment1, comment2);
        
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.findTopLevelCommentsByPostFirstPage(1L)).thenReturn(comments);
        when(commentMapper.mapToDto(any(Comment.class))).thenReturn(testCommentDto);
        when(cursorUtil.encodeCursor(any(Instant.class), any(Long.class))).thenReturn("next-cursor");

        // Act
        CursorPageResponse<CommentsDto> result = commentService.getCommentsForPost(1L, null, 1);

        // Assert
        assertTrue(result.isHasMore());
        assertEquals("next-cursor", result.getNextCursor());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void deleteComment_ShouldDecrementPostCommentCount() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setEnabled(true);

        Post post = new Post();
        post.setPostId(1L);
        post.setPostName("Test Post");
        post.setDescription("Test Description");
        post.setUser(user);
        post.setVoteCount(0);
        post.setCommentCount(2); // Start with 2 comments

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setPost(post);
        comment.setUser(user);
        comment.setVoteCount(0);
        comment.setReplyCount(0);
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(authService.getCurrentUser()).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Act
        commentService.deleteComment(1L);

        // Assert
        verify(commentRepository).save(argThat(savedComment -> 
            savedComment.isDeleted() && savedComment.getId().equals(1L)
        ));
        verify(postRepository).save(argThat(savedPost -> 
            savedPost.getCommentCount() == 1 && savedPost.getPostId().equals(1L)
        ));
    }

    @Test
    void save_ShouldIncrementPostCommentCount() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setEnabled(true);

        Post post = new Post();
        post.setPostId(1L);
        post.setPostName("Test Post");
        post.setDescription("Test Description");
        post.setUser(user);
        post.setVoteCount(0);
        post.setCommentCount(1); // Start with 1 comment

        CommentsDto commentsDto = new CommentsDto();
        commentsDto.setPostId(1L);
        commentsDto.setText("New comment");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(authService.getCurrentUser()).thenReturn(user);
        when(commentMapper.map(any(CommentsDto.class), any(Post.class), any(User.class)))
                .thenReturn(new Comment());
        when(mailContentBuilder.build(anyString())).thenReturn("Email content");
        when(postRepository.save(any(Post.class))).thenReturn(post);
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // Act
        commentService.save(commentsDto);

        // Assert
        verify(postRepository).save(argThat(savedPost -> 
            savedPost.getCommentCount() == 2 && savedPost.getPostId().equals(1L)
        ));
    }
} 