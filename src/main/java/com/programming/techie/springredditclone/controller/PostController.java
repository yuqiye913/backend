package com.programming.techie.springredditclone.controller;

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
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return status(HttpStatus.OK).body(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return status(HttpStatus.OK).body(postService.getPost(id));
    }

    @GetMapping(params = "subredditId")
    public ResponseEntity<List<PostResponse>> getPostsBySubreddit(@RequestParam Long subredditId) {
        return status(HttpStatus.OK).body(postService.getPostsBySubreddit(subredditId));
    }

    @GetMapping(params = "username")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(@RequestParam String username) {
        return status(HttpStatus.OK).body(postService.getPostsByUsername(username));
    }

    @GetMapping("/by-subreddits")
    public ResponseEntity<List<PostResponse>> getPostsByMultipleSubreddits(@RequestParam List<String> subredditNames) {
        return status(HttpStatus.OK).body(postService.getPostsByMultipleSubreddits(subredditNames));
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
