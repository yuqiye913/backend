package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.VoteStatusDto;
import com.programming.techie.springredditclone.event.PostLikedEvent;
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
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.BlockValidationService;
import com.programming.techie.springredditclone.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.programming.techie.springredditclone.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final BlockService blockService;
    private final BlockValidationService blockValidationService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + postId));
        
        User currentUser = authService.getCurrentUser();
        User postOwner = post.getUser();
        
        // Use BlockValidationService for cleaner validation
        blockValidationService.validateCanInteract(postOwner);
        
        Optional<Vote> existingVote = voteRepository.findByPostAndUser(post, currentUser);
        
        if (existingVote.isPresent()) {
            // User has already liked - do nothing (idempotent)
            return;
        } else {
            // User hasn't liked yet - create new like
            post.setVoteCount(post.getVoteCount() + 1);
            Vote newVote = Vote.builder()
                    .voteType(UPVOTE)
                    .post(post)
                    .user(currentUser)
                    .build();
            voteRepository.save(newVote);
            
            // Publish event for new like
            eventPublisher.publishEvent(new PostLikedEvent(this, currentUser, post.getUser(), post));
        }
        
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + postId));
        
        User currentUser = authService.getCurrentUser();
        
        Optional<Vote> existingVote = voteRepository.findByPostAndUser(post, currentUser);
        
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            // Remove the like and adjust count
            post.setVoteCount(post.getVoteCount() - 1);
            voteRepository.delete(vote);
            postRepository.save(post);
        } else {
            // User hasn't liked - do nothing (idempotent)
            return;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VoteStatusDto getPostLikeStatus(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + postId));
        
        User currentUser = authService.getCurrentUser();
        Optional<Vote> existingVote = voteRepository.findByPostAndUser(post, currentUser);
        
        return VoteStatusDto.builder()
                .isLiked(existingVote.isPresent())
                .likeCount(post.getVoteCount())
                .build();
    }

    @Override
    @Transactional
    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new SpringRedditException("Comment Not Found with ID - " + commentId));
        
        User currentUser = authService.getCurrentUser();
        User commentOwner = comment.getUser();
        
        // Use BlockValidationService for cleaner validation
        blockValidationService.validateCanInteract(commentOwner);
        
        Optional<Vote> existingVote = voteRepository.findByCommentAndUser(comment, currentUser);
        
        if (existingVote.isPresent()) {
            // User has already liked - do nothing (idempotent)
            return;
        } else {
            // User hasn't liked yet - create new like
            comment.setVoteCount(comment.getVoteCount() + 1);
            Vote newVote = Vote.builder()
                    .voteType(UPVOTE)
                    .comment(comment)
                    .user(currentUser)
                    .build();
            voteRepository.save(newVote);
        }
        
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new SpringRedditException("Comment Not Found with ID - " + commentId));
        
        User currentUser = authService.getCurrentUser();
        
        Optional<Vote> existingVote = voteRepository.findByCommentAndUser(comment, currentUser);
        
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            // Remove the like and adjust count
            comment.setVoteCount(comment.getVoteCount() - 1);
            voteRepository.delete(vote);
            commentRepository.save(comment);
        } else {
            // User hasn't liked - do nothing (idempotent)
            return;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VoteStatusDto getCommentLikeStatus(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new SpringRedditException("Comment Not Found with ID - " + commentId));
        
        User currentUser = authService.getCurrentUser();
        Optional<Vote> existingVote = voteRepository.findByCommentAndUser(comment, currentUser);
        
        return VoteStatusDto.builder()
                .isLiked(existingVote.isPresent())
                .likeCount(comment.getVoteCount())
                .build();
    }
} 