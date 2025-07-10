package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.CommentVoteRequest;
import com.programming.techie.springredditclone.dto.VoteDto;
import com.programming.techie.springredditclone.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Void> vote(@RequestBody VoteDto voteDto) {
        voteService.vote(voteDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping("/comment")
    public ResponseEntity<Void> voteOnComment(@Valid @RequestBody CommentVoteRequest commentVoteRequest) {
        voteService.voteOnComment(commentVoteRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/comment/{commentId}/count")
    public ResponseEntity<Integer> getCommentVoteCount(@PathVariable Long commentId) {
        Integer voteCount = voteService.getCommentVoteCount(commentId);
        return ResponseEntity.ok(voteCount);
    }
}
