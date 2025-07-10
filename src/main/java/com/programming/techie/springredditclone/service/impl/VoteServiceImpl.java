package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.CommentVoteRequest;
import com.programming.techie.springredditclone.dto.VoteDto;
import com.programming.techie.springredditclone.exceptions.PostNotFoundException;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.Vote;
import com.programming.techie.springredditclone.model.VoteType;
import com.programming.techie.springredditclone.repository.CommentRepository;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.VoteRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.VoteService;
import com.programming.techie.springredditclone.mapper.VoteMapper;
import lombok.AllArgsConstructor;
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
    private final VoteMapper voteMapper;

    @Override
    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + voteDto.getPostId()));
        
        Optional<Vote> existingVote = voteRepository.findByPostAndUser(post, authService.getCurrentUser());
        
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
            if (UPVOTE.equals(voteDto.getVoteType())) {
                post.setVoteCount(post.getVoteCount() + 1);
            } else {
                post.setVoteCount(post.getVoteCount() - 1);
            }
            voteRepository.save(voteMapper.mapToVote(voteDto, post, authService.getCurrentUser()));
        }
        
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void voteOnComment(CommentVoteRequest commentVoteRequest) {
        Comment comment = commentRepository.findById(commentVoteRequest.getCommentId())
                .orElseThrow(() -> new SpringRedditException("Comment Not Found with ID - " + commentVoteRequest.getCommentId()));
        
        Optional<Vote> existingVote = voteRepository.findByCommentAndUser(comment, authService.getCurrentUser());
        
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
                    .user(authService.getCurrentUser())
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