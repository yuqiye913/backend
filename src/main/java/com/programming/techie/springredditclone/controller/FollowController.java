package com.programming.techie.springredditclone.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.programming.techie.springredditclone.service.FollowService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.programming.techie.springredditclone.dto.FollowRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> followUser(@Valid @RequestBody FollowRequestDto followRequest) {
        followService.followUser(followRequest);
        return ResponseEntity.ok("Successfully followed user");
    }
}
