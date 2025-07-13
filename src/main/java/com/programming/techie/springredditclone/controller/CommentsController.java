package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.CommentsDto;
import com.programming.techie.springredditclone.dto.CreateCommentRequest;
import com.programming.techie.springredditclone.dto.CursorPageResponse;
import com.programming.techie.springredditclone.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
@Validated
public class CommentsController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@Valid @RequestBody CreateCommentRequest createCommentRequest) {
        commentService.createComment(createCommentRequest);
        return new ResponseEntity<>(CREATED);
    }

    @GetMapping(params = "postId")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@RequestParam Long postId) {
        return ResponseEntity.status(OK)
                .body(commentService.getAllCommentsForPost(postId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<CursorPageResponse<CommentsDto>> getCommentsForPost(
            @PathVariable Long postId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        if (limit != null && limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        return ResponseEntity.status(OK)
                .body(commentService.getCommentsForPost(postId, cursor, limit));
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<CursorPageResponse<CommentsDto>> getCommentsForUser(
            @PathVariable String userName,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        if (limit != null && limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        return ResponseEntity.status(OK)
                .body(commentService.getCommentsForUser(userName, cursor, limit));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<CursorPageResponse<CommentsDto>> getRepliesForComment(
            @PathVariable Long commentId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        if (limit != null && limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        return ResponseEntity.status(OK)
                .body(commentService.getRepliesForComment(commentId, cursor, limit));
    }

    @GetMapping(params = "userName")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForUser(@RequestParam String userName){
        return ResponseEntity.status(OK)
                .body(commentService.getAllCommentsForUser(userName));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.status(OK).build();
    }

}
