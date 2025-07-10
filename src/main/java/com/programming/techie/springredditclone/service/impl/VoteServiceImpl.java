package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.CommentVoteRequest;
import com.programming.techie.springredditclone.dto.VoteDto;
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
import com.programming.techie.springredditclone.service.VoteService;
import com.programming.techie.springredditclone.mapper.VoteMapper;
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
    private final VoteMapper voteMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + voteDto.getPostId()));
        
        User currentUser = authService.getCurrentUser();
        User postOwner = post.getUser();
        
        // Check if current user is blocked by post owner or has blocked post owner
        if (blockService.isBlockedByUser(postOwner.getUserId()) || blockService.hasBlockedUser(postOwner.getUserId())) {
            throw new SpringRedditException("Cannot vote on this post due to block restrictions");
        }
        
        Optional<Vote> existingVote = voteRepository.findByPostAndUser(post, currentUser);
        
        if (existingVote.isPresent()) {
            // User has already voted - update existing vote
            Vote vote = existingVote.get();
            VoteType oldVoteType = vote.getVoteType();
            VoteType newVoteType = voteDto.getVoteType();
            
            if (oldVoteType.equals(newVoteType)) {
                throw new SpringRedditException("You have already " + newVoteType + "'d for this post");
            }
            
            // Update vote count: remove old vote, add new vote
            post.setVoteCount(post.getVoteCount() - oldVoteType.getDirection() + newVoteType.getDirection());
            
            // Update the existing vote
            vote.setVoteType(newVoteType);
            voteRepository.save(vote);
        } else {
            // User hasn't voted yet - create new vote
            boolean isNewUpvote = UPVOTE.equals(voteDto.getVoteType());
            if (isNewUpvote) {
                post.setVoteCount(post.getVoteCount() + 1);
            } else {
                post.setVoteCount(post.getVoteCount() - 1);
            }
            voteRepository.save(voteMapper.mapToVote(voteDto, post, currentUser));
            
            // Publish event for new upvote (like)
            if (isNewUpvote) {
                eventPublisher.publishEvent(new PostLikedEvent(this, currentUser, post.getUser(), post));
            }
        }
        
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void voteOnComment(CommentVoteRequest commentVoteRequest) {
        Comment comment = commentRepository.findById(commentVoteRequest.getCommentId())
                .orElseThrow(() -> new SpringRedditException("Comment Not Found with ID - " + commentVoteRequest.getCommentId()));
        
        User currentUser = authService.getCurrentUser();
        User commentOwner = comment.getUser();
        
        // Check if current user is blocked by comment owner or has blocked comment owner
        if (blockService.isBlockedByUser(commentOwner.getUserId()) || blockService.hasBlockedUser(commentOwner.getUserId())) {
            throw new SpringRedditException("Cannot vote on this comment due to block restrictions");
        }
        
        Optional<Vote> existingVote = voteRepository.findByCommentAndUser(comment, currentUser);
        
        if (existingVote.isPresent()) {
            // User has already voted - update existing vote
            Vote vote = existingVote.get();
            VoteType oldVoteType = vote.getVoteType();
            VoteType newVoteType = commentVoteRequest.getVoteType();
            
            if (oldVoteType.equals(newVoteType)) {
                throw new SpringRedditException("You have already " + newVoteType + "'d for this comment");
            }
            
            // Update vote count: remove old vote, add new vote
            comment.setVoteCount(comment.getVoteCount() - oldVoteType.getDirection() + newVoteType.getDirection());
            
            // Update the existing vote
            vote.setVoteType(newVoteType);
            voteRepository.save(vote);
        } else {
            // User hasn't voted yet - create new vote
            if (UPVOTE.equals(commentVoteRequest.getVoteType())) {
                comment.setVoteCount(comment.getVoteCount() + 1);
            } else {
                comment.setVoteCount(comment.getVoteCount() - 1);
            }
            
            Vote newVote = Vote.builder()
                    .voteType(commentVoteRequest.getVoteType())
                    .comment(comment)
                    .user(currentUser)
                    .build();
            voteRepository.save(newVote);
        }
        
        commentRepository.save(comment);
    }

    @Override
    public Integer getCommentVoteCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new SpringRedditException("Comment Not Found with ID - " + commentId));
        return comment.getVoteCount();
    }
} 