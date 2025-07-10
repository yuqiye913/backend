package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    // Post voting methods
    Optional<Vote> findByPostAndUser(Post post, User currentUser);
    
    // Comment voting methods
    Optional<Vote> findByCommentAndUser(Comment comment, User currentUser);
    
    // Vote count methods
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.post = :post AND v.voteType = 'UPVOTE'")
    Long countUpvotesByPost(@Param("post") Post post);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.post = :post AND v.voteType = 'DOWNVOTE'")
    Long countDownvotesByPost(@Param("post") Post post);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.comment = :comment AND v.voteType = 'UPVOTE'")
    Long countUpvotesByComment(@Param("comment") Comment comment);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.comment = :comment AND v.voteType = 'DOWNVOTE'")
    Long countDownvotesByComment(@Param("comment") Comment comment);
    
    // Vote statistics
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.user = :user")
    Long countVotesByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.user = :user AND v.voteType = 'UPVOTE'")
    Long countUpvotesByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.user = :user AND v.voteType = 'DOWNVOTE'")
    Long countDownvotesByUser(@Param("user") User user);
}
