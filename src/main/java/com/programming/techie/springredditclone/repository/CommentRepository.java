package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Basic comment queries
    List<Comment> findByPost(Post post);
    List<Comment> findAllByUser(User user);
    
    // Threading/replies queries
    List<Comment> findByParentComment(Comment parentComment);
    List<Comment> findByParentCommentIsNullAndPost(Post post); // Top-level comments only
    List<Comment> findByParentCommentIsNotNullAndPost(Post post); // Reply comments only
    
    // Comment status queries
    List<Comment> findByPostAndDeletedFalse(Post post);
    List<Comment> findByPostAndIsHiddenFalse(Post post);
    List<Comment> findByUserAndDeletedFalse(User user);
    
    // Vote count queries
    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.parentComment IS NULL AND c.deleted = false ORDER BY c.voteCount DESC")
    List<Comment> findTopLevelCommentsByPostOrderByVoteCountDesc(@Param("post") Post post);
    
    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.parentComment IS NULL AND c.deleted = false ORDER BY c.createdDate DESC")
    List<Comment> findTopLevelCommentsByPostOrderByCreatedDateDesc(@Param("post") Post post);
    
    // Reply count queries
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment = :parentComment AND c.deleted = false")
    Long countRepliesByParentComment(@Param("parentComment") Comment parentComment);
    
    // Comment tree queries
    @Query("SELECT c FROM Comment c WHERE c.parentComment = :parentComment AND c.deleted = false ORDER BY c.createdDate ASC")
    List<Comment> findRepliesByParentCommentOrderByDate(@Param("parentComment") Comment parentComment);
    
    // Search comments
    @Query("SELECT c FROM Comment c WHERE c.text LIKE %:searchTerm% AND c.deleted = false")
    List<Comment> searchCommentsByText(@Param("searchTerm") String searchTerm);
    
    // Comment statistics
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post AND c.deleted = false")
    Long countCommentsByPost(@Param("post") Post post);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user = :user AND c.deleted = false")
    Long countCommentsByUser(@Param("user") User user);
    
    // Check if comment exists and is not deleted
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.deleted = false")
    Optional<Comment> findByIdAndNotDeleted(@Param("commentId") Long commentId);
    
    // Cursor-based pagination queries
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parentComment IS NULL AND c.deleted = false AND c.id > :cursor ORDER BY c.id ASC")
    List<Comment> findTopLevelCommentsByPostWithCursor(@Param("postId") Long postId, @Param("cursor") Long cursor);
    
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parentComment IS NULL AND c.deleted = false ORDER BY c.id ASC")
    List<Comment> findTopLevelCommentsByPostFirstPage(@Param("postId") Long postId);
    
    @Query("SELECT c FROM Comment c WHERE c.user.username = :userName AND c.deleted = false AND c.id > :cursor ORDER BY c.id ASC")
    List<Comment> findCommentsByUserWithCursor(@Param("userName") String userName, @Param("cursor") Long cursor);
    
    @Query("SELECT c FROM Comment c WHERE c.user.username = :userName AND c.deleted = false ORDER BY c.id ASC")
    List<Comment> findCommentsByUserFirstPage(@Param("userName") String userName);
    
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :commentId AND c.deleted = false AND c.id > :cursor ORDER BY c.id ASC")
    List<Comment> findRepliesByCommentWithCursor(@Param("commentId") Long commentId, @Param("cursor") Long cursor);
    
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :commentId AND c.deleted = false ORDER BY c.id ASC")
    List<Comment> findRepliesByCommentFirstPage(@Param("commentId") Long commentId);
}
