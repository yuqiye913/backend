package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.CursorPageResponse;
import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.dto.PostResponse;
import com.programming.techie.springredditclone.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody PostRequest postRequest) {
        postService.save(postRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponse<PostResponse>> getAllPosts(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit) {
        
        return status(HttpStatus.OK).body(postService.getAllPosts(cursor, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return status(HttpStatus.OK).body(postService.getPost(id));
    }

    @GetMapping(params = "subredditId")
    public ResponseEntity<CursorPageResponse<PostResponse>> getPostsBySubreddit(
            @RequestParam Long subredditId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit) {
        
        return status(HttpStatus.OK).body(postService.getPostsBySubreddit(subredditId, cursor, limit));
    }

    @GetMapping(params = "username")
    public ResponseEntity<CursorPageResponse<PostResponse>> getPostsByUsername(
            @RequestParam String username,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit) {
        
        return status(HttpStatus.OK).body(postService.getPostsByUsername(username, cursor, limit));
    }

    @GetMapping("/by-subreddits")
    public ResponseEntity<List<PostResponse>> getPostsByMultipleSubreddits(@RequestParam List<String> subredditNames) {
        return status(HttpStatus.OK).body(postService.getPostsByMultipleSubreddits(subredditNames));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> searchPosts(
            @RequestParam String query) {
        List<PostResponse> posts = postService.searchPosts(query);
        return status(HttpStatus.OK).body(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId, @Valid @RequestBody PostRequest postRequest) {
        postService.updatePost(postId, postRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
