package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.VoteDto;
import com.programming.techie.springredditclone.exceptions.PostNotFoundException;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.Vote;
import com.programming.techie.springredditclone.model.VoteType;
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


} 