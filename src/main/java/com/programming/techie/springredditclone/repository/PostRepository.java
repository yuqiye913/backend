package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.Subreddit;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findBySubredditsContaining(Subreddit subreddit);
    Page<Post> findBySubredditsContaining(Subreddit subreddit, Pageable pageable);
    List<Post> findByUser(User user);
    Page<Post> findByUser(User user, Pageable pageable);
    
    // Find posts that belong to any of the specified subreddits
    List<Post> findBySubredditsIn(Set<Subreddit> subreddits);
    
    // Cursor-based pagination methods
    @Query("SELECT p FROM Post p WHERE (p.createdDate, p.postId) < (:createdDate, :postId) ORDER BY p.createdDate DESC, p.postId DESC")
    List<Post> findAllWithCursor(@Param("createdDate") Instant createdDate, @Param("postId") Long postId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE (p.createdDate, p.postId) < (:createdDate, :postId) AND :subreddit MEMBER OF p.subreddits ORDER BY p.createdDate DESC, p.postId DESC")
    List<Post> findBySubredditWithCursor(@Param("subreddit") Subreddit subreddit, @Param("createdDate") Instant createdDate, @Param("postId") Long postId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE (p.createdDate, p.postId) < (:createdDate, :postId) AND p.user = :user ORDER BY p.createdDate DESC, p.postId DESC")
    List<Post> findByUserWithCursor(@Param("user") User user, @Param("createdDate") Instant createdDate, @Param("postId") Long postId, org.springframework.data.domain.Pageable pageable);
    
    // Methods to check if there are more results
    @Query("SELECT COUNT(p) > 0 FROM Post p WHERE (p.createdDate, p.postId) < (:createdDate, :postId)")
    boolean hasMoreResults(@Param("createdDate") Instant createdDate, @Param("postId") Long postId);
    
    @Query("SELECT COUNT(p) > 0 FROM Post p WHERE (p.createdDate, p.postId) < (:createdDate, :postId) AND :subreddit MEMBER OF p.subreddits")
    boolean hasMoreResultsBySubreddit(@Param("subreddit") Subreddit subreddit, @Param("createdDate") Instant createdDate, @Param("postId") Long postId);
    
    @Query("SELECT COUNT(p) > 0 FROM Post p WHERE (p.createdDate, p.postId) < (:createdDate, :postId) AND p.user = :user")
    boolean hasMoreResultsByUser(@Param("user") User user, @Param("createdDate") Instant createdDate, @Param("postId") Long postId);
}
