package com.programming.techie.springredditclone.repository.content;

import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@Rollback
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private Comment testReply;

    @BeforeEach
    void setUp() {
        // Clean up any existing data
        entityManager.getEntityManager().createQuery("DELETE FROM Comment").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Post").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM User").executeUpdate();
        entityManager.flush();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setEnabled(true);
        testUser.setCreated(Instant.now());
        testUser = entityManager.persistAndFlush(testUser);

        // Create test post
        testPost = new Post();
        testPost.setPostName("Test Post");
        testPost.setDescription("Test Description");
        testPost.setUser(testUser);
        testPost.setCreatedDate(Instant.now());
        testPost.setVoteCount(0);
        testPost = entityManager.persistAndFlush(testPost);

        // Create test comment
        testComment = new Comment();
        testComment.setText("Test comment");
        testComment.setPost(testPost);
        testComment.setUser(testUser);
        testComment.setCreatedDate(Instant.now());
        testComment.setVoteCount(0);
        testComment.setReplyCount(0);
        testComment.setParentComment(null); // Explicitly set as top-level comment
        testComment.setDeleted(false); // Explicitly set isDeleted
        testComment = entityManager.persistAndFlush(testComment);

        // Create test reply
        testReply = new Comment();
        testReply.setText("Test reply");
        testReply.setPost(testPost);
        testReply.setUser(testUser);
        testReply.setParentComment(testComment);
        testReply.setCreatedDate(Instant.now());
        testReply.setVoteCount(0);
        testReply.setReplyCount(0);
        testReply.setDeleted(false); // Explicitly set isDeleted
        testReply = entityManager.persistAndFlush(testReply);
    }

    @Test
    void findByPost_ShouldReturnAllCommentsForPost() {
        // Act
        List<Comment> comments = commentRepository.findByPost(testPost);

        // Assert
        assertEquals(2, comments.size());
        assertTrue(comments.stream().anyMatch(c -> c.getText().equals("Test comment")));
        assertTrue(comments.stream().anyMatch(c -> c.getText().equals("Test reply")));
    }

    @Test
    void findAllByUser_ShouldReturnAllCommentsByUser() {
        // Act
        List<Comment> comments = commentRepository.findAllByUser(testUser);

        // Assert
        assertEquals(2, comments.size());
        assertTrue(comments.stream().allMatch(c -> c.getUser().equals(testUser)));
    }

    @Test
    void findByParentComment_ShouldReturnReplies() {
        // Act
        List<Comment> replies = commentRepository.findByParentComment(testComment);

        // Assert
        assertEquals(1, replies.size());
        assertEquals("Test reply", replies.get(0).getText());
        assertEquals(testComment.getId(), replies.get(0).getParentComment().getId());
    }

    @Test
    void findByParentCommentIsNullAndPost_ShouldReturnTopLevelComments() {
        // Act
        List<Comment> topLevelComments = commentRepository.findByParentCommentIsNullAndPost(testPost);

        // Assert
        assertEquals(1, topLevelComments.size());
        assertEquals("Test comment", topLevelComments.get(0).getText());
        assertNull(topLevelComments.get(0).getParentComment());
    }

    @Test
    void findByParentCommentIsNotNullAndPost_ShouldReturnReplyComments() {
        // Act
        List<Comment> replyComments = commentRepository.findByParentCommentIsNotNullAndPost(testPost);

        // Assert
        assertEquals(1, replyComments.size());
        assertEquals("Test reply", replyComments.get(0).getText());
        assertNotNull(replyComments.get(0).getParentComment());
    }

    @Test
    void findByPostAndDeletedFalse_ShouldReturnNonDeletedComments() {
        // Arrange
        Comment deletedComment = new Comment();
        deletedComment.setText("Deleted comment");
        deletedComment.setPost(testPost);
        deletedComment.setUser(testUser);
        deletedComment.setCreatedDate(Instant.now());
        deletedComment.setVoteCount(0);
        deletedComment.setReplyCount(0);
        deletedComment.setDeleted(true);
        entityManager.persistAndFlush(deletedComment);
        
        // Debug: Check if the deleted flag was set correctly
        System.out.println("Deleted comment isDeleted after setting: " + deletedComment.isDeleted());
        Comment persistedDeletedComment = entityManager.find(Comment.class, deletedComment.getId());
        System.out.println("Persisted deleted comment isDeleted: " + persistedDeletedComment.isDeleted());

        // Act
        List<Comment> comments = commentRepository.findByPostAndDeletedFalse(testPost);

        // Debug: Print actual results
        System.out.println("\n=== DEBUG findByPostAndDeletedFalse ===");
        System.out.println("Total comments found: " + comments.size());
        for (int i = 0; i < comments.size(); i++) {
            Comment c = comments.get(i);
            System.out.println(String.format("Comment %d: ID=%d, Text='%s', IsDeleted=%s", 
                i, c.getId(), c.getText(), c.isDeleted()));
        }
        System.out.println("==========================================\n");

        // Assert
        assertEquals(2, comments.size()); // Original 2 comments, excluding deleted one
        assertTrue(comments.stream().noneMatch(Comment::isDeleted));
    }

    @Test
    void findTopLevelCommentsByPostOrderByVoteCountDesc_ShouldReturnOrderedComments() {
        // Arrange
        Comment highVoteComment = new Comment();
        highVoteComment.setText("High vote comment");
        highVoteComment.setPost(testPost);
        highVoteComment.setUser(testUser);
        highVoteComment.setCreatedDate(Instant.now());
        highVoteComment.setVoteCount(10);
        highVoteComment.setReplyCount(0);
        highVoteComment.setParentComment(null); // Explicitly set as top-level comment
        final Comment persistedHighVoteComment = entityManager.persistAndFlush(highVoteComment);

        // Act
        List<Comment> comments = commentRepository.findTopLevelCommentsByPostOrderByVoteCountDesc(testPost);

        // Debug: Print actual results
        System.out.println("\n=== DEBUG INFO ===");
        System.out.println("Total comments found: " + comments.size());
        for (int i = 0; i < comments.size(); i++) {
            Comment c = comments.get(i);
            System.out.println(String.format("Comment %d: ID=%d, VoteCount=%d, Text='%s', ParentComment=%s, IsDeleted=%s", 
                i, c.getId(), c.getVoteCount(), c.getText(), 
                c.getParentComment() == null ? "null" : c.getParentComment().getId(),
                c.isDeleted()));
        }
        System.out.println("HighVoteComment: ID=" + highVoteComment.getId() + ", VoteCount=" + highVoteComment.getVoteCount());
        System.out.println("TestComment: ID=" + testComment.getId() + ", VoteCount=" + testComment.getVoteCount());
        System.out.println("==================\n");

        // Assert
        assertEquals(2, comments.size(), "Should return exactly 2 top-level comments");
        
        // Verify all comments are top-level (no parent)
        assertTrue(comments.stream().allMatch(c -> c.getParentComment() == null), 
            "All comments should be top-level (parentComment == null)");
        
        // Verify all comments are not deleted
        assertTrue(comments.stream().allMatch(c -> !c.isDeleted()), 
            "All comments should not be deleted");
        
        // Verify ordering by vote count (DESC) - this is the failing assertion
        for (int i = 0; i < comments.size() - 1; i++) {
            int currentVotes = comments.get(i).getVoteCount();
            int nextVotes = comments.get(i + 1).getVoteCount();
            assertTrue(currentVotes >= nextVotes,
                String.format("Comment at index %d (ID=%d, votes=%d) should have >= votes than comment at index %d (ID=%d, votes=%d)",
                    i, comments.get(i).getId(), currentVotes, 
                    i + 1, comments.get(i + 1).getId(), nextVotes));
        }
        
        // Verify that both comments are in the results
        assertTrue(comments.stream().anyMatch(c -> c.getId().equals(persistedHighVoteComment.getId())), 
            "High vote comment should be in results");
        assertTrue(comments.stream().anyMatch(c -> c.getId().equals(testComment.getId())), 
            "Test comment should be in results");
    }

    @Test
    void findTopLevelCommentsByPostOrderByCreatedDateDesc_ShouldReturnOrderedComments() {
        // Arrange
        Comment newerComment = new Comment();
        newerComment.setText("Newer comment");
        newerComment.setPost(testPost);
        newerComment.setUser(testUser);
        newerComment.setCreatedDate(Instant.now().plusSeconds(3600)); // 1 hour later
        newerComment.setVoteCount(0);
        newerComment.setReplyCount(0);
        newerComment.setParentComment(null); // Explicitly set as top-level comment
        newerComment = entityManager.persistAndFlush(newerComment);

        // Act
        List<Comment> comments = commentRepository.findTopLevelCommentsByPostOrderByCreatedDateDesc(testPost);

        // Assert
        assertEquals(2, comments.size());
        assertEquals(newerComment.getId(), comments.get(0).getId()); // Newest first
        assertEquals(testComment.getId(), comments.get(1).getId()); // Older second
        assertTrue(comments.get(0).getCreatedDate().isAfter(comments.get(1).getCreatedDate()) ||
                   comments.get(0).getCreatedDate().equals(comments.get(1).getCreatedDate()));
    }

    @Test
    void countRepliesByParentComment_ShouldReturnCorrectCount() {
        // Act
        Long replyCount = commentRepository.countRepliesByParentComment(testComment);

        // Assert
        assertEquals(1L, replyCount);
    }

    @Test
    void findRepliesByParentCommentOrderByDate_ShouldReturnOrderedReplies() {
        // Act
        List<Comment> replies = commentRepository.findRepliesByParentCommentOrderByDate(testComment);

        // Assert
        assertEquals(1, replies.size());
        assertEquals("Test reply", replies.get(0).getText());
    }

    @Test
    void searchCommentsByText_ShouldReturnMatchingComments() {
        // Act
        List<Comment> comments = commentRepository.searchCommentsByText("Test");

        // Assert
        assertEquals(2, comments.size());
        assertTrue(comments.stream().allMatch(c -> c.getText().contains("Test")));
    }

    @Test
    void countCommentsByPost_ShouldReturnCorrectCount() {
        // Act
        Long commentCount = commentRepository.countCommentsByPost(testPost);

        // Assert
        assertEquals(2L, commentCount);
    }

    @Test
    void countCommentsByUser_ShouldReturnCorrectCount() {
        // Act
        Long commentCount = commentRepository.countCommentsByUser(testUser);

        // Assert
        assertEquals(2L, commentCount);
    }

    @Test
    void findByIdAndNotDeleted_ValidComment_ShouldReturnComment() {
        // Act
        Optional<Comment> result = commentRepository.findByIdAndNotDeleted(testComment.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testComment.getId(), result.get().getId());
    }

    @Test
    void findByIdAndNotDeleted_DeletedComment_ShouldReturnEmpty() {
        // Arrange
        testComment.setDeleted(true); // Using Lombok generated setter
        entityManager.persistAndFlush(testComment);

        // Act
        Optional<Comment> result = commentRepository.findByIdAndNotDeleted(testComment.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findTopLevelCommentsByPostWithCursor_ShouldReturnCommentsAfterCursor() {
        // Arrange
        Comment comment2 = new Comment();
        comment2.setText("Comment 2");
        comment2.setPost(testPost);
        comment2.setUser(testUser);
        comment2.setCreatedDate(Instant.now().plusSeconds(1));
        comment2.setVoteCount(0);
        comment2.setReplyCount(0);
        comment2.setParentComment(null); // Top-level comment
        comment2 = entityManager.persistAndFlush(comment2);

        // Act
        List<Comment> comments = commentRepository.findTopLevelCommentsByPostWithCursor(testPost.getPostId(), testComment.getId());

        // Assert
        assertEquals(1, comments.size());
        assertEquals(comment2.getId(), comments.get(0).getId());
    }

    @Test
    void findTopLevelCommentsByPostFirstPage_ShouldReturnAllTopLevelComments() {
        // Act
        List<Comment> comments = commentRepository.findTopLevelCommentsByPostFirstPage(testPost.getPostId());

        // Assert
        assertEquals(1, comments.size());
        assertEquals(testComment.getId(), comments.get(0).getId());
        assertNull(comments.get(0).getParentComment());
    }

    @Test
    void findCommentsByUserWithCursor_ShouldReturnCommentsAfterCursor() {
        // Arrange
        Comment comment2 = new Comment();
        comment2.setText("Comment 2");
        comment2.setPost(testPost);
        comment2.setUser(testUser);
        comment2.setCreatedDate(Instant.now().plusSeconds(1));
        comment2.setVoteCount(0);
        comment2.setReplyCount(0);
        comment2.setDeleted(false);
        final Comment persistedComment2 = entityManager.persistAndFlush(comment2);

        // Act
        List<Comment> comments = commentRepository.findCommentsByUserWithCursor(testUser.getUsername(), testComment.getId());

        // Debug: Print actual results
        System.out.println("\n=== DEBUG findCommentsByUserWithCursor ===");
        System.out.println("Total comments found: " + comments.size());
        for (int i = 0; i < comments.size(); i++) {
            Comment c = comments.get(i);
            System.out.println(String.format("Comment %d: ID=%d, Text='%s', CreatedDate=%s", 
                i, c.getId(), c.getText(), c.getCreatedDate()));
        }
        System.out.println("Cursor ID: " + testComment.getId());
        System.out.println("Comment2 ID: " + persistedComment2.getId());
        System.out.println("==========================================\n");

        // Assert
        assertEquals(2, comments.size()); // Should return both testReply and comment2
        assertTrue(comments.stream().anyMatch(c -> c.getId().equals(testReply.getId())));
        assertTrue(comments.stream().anyMatch(c -> c.getId().equals(persistedComment2.getId())));
    }

    @Test
    void findCommentsByUserFirstPage_ShouldReturnAllUserComments() {
        // Act
        List<Comment> comments = commentRepository.findCommentsByUserFirstPage(testUser.getUsername());

        // Assert
        assertEquals(2, comments.size());
        assertTrue(comments.stream().allMatch(c -> c.getUser().getUsername().equals(testUser.getUsername())));
    }

    @Test
    void findRepliesByCommentWithCursor_ShouldReturnRepliesAfterCursor() {
        // Arrange
        Comment reply2 = new Comment();
        reply2.setText("Reply 2");
        reply2.setPost(testPost);
        reply2.setUser(testUser);
        reply2.setParentComment(testComment);
        reply2.setCreatedDate(Instant.now().plusSeconds(1));
        reply2.setVoteCount(0);
        reply2.setReplyCount(0);
        reply2 = entityManager.persistAndFlush(reply2);

        // Act
        List<Comment> replies = commentRepository.findRepliesByCommentWithCursor(testComment.getId(), testReply.getId());

        // Assert
        assertEquals(1, replies.size());
        assertEquals(reply2.getId(), replies.get(0).getId());
    }

    @Test
    void findRepliesByCommentFirstPage_ShouldReturnAllReplies() {
        // Act
        List<Comment> replies = commentRepository.findRepliesByCommentFirstPage(testComment.getId());

        // Assert
        assertEquals(1, replies.size());
        assertEquals(testReply.getId(), replies.get(0).getId());
        assertEquals(testComment.getId(), replies.get(0).getParentComment().getId());
    }
}