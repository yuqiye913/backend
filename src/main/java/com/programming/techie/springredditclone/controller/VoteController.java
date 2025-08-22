package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.VoteStatusDto;
import com.programming.techie.springredditclone.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
public class VoteController {

    private final VoteService voteService;

    // Post like endpoints
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        voteService.likePost(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        voteService.unlikePost(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/posts/{postId}/like")
    public ResponseEntity<VoteStatusDto> getPostLikeStatus(@PathVariable Long postId) {
        VoteStatusDto likeStatus = voteService.getPostLikeStatus(postId);
        return ResponseEntity.ok(likeStatus);
    }
    
    // Comment like endpoints
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId) {
        voteService.likeComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        voteService.unlikeComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/comments/{commentId}/like")
    public ResponseEntity<VoteStatusDto> getCommentLikeStatus(@PathVariable Long commentId) {
        VoteStatusDto likeStatus = voteService.getCommentLikeStatus(commentId);
        return ResponseEntity.ok(likeStatus);
    }
}
