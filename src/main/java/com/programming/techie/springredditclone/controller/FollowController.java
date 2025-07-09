package com.programming.techie.springredditclone.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.programming.techie.springredditclone.service.FollowService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.dto.GetFollowersDto;
import com.programming.techie.springredditclone.dto.FollowerCountDto;
import com.programming.techie.springredditclone.dto.FollowingCountDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import jakarta.validation.Valid;
import java.util.List;

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

    @DeleteMapping("/follow/{followingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unfollowUser(@PathVariable Long followingId) {
        followService.unfollowUser(followingId);
        return ResponseEntity.ok("Successfully unfollowed user");
    }

    @GetMapping("/followers/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GetFollowersDto>> getFollowersByUserId(@PathVariable Long userId) {
        List<GetFollowersDto> followers = followService.getFollowersByUserId(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/followers/count/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FollowerCountDto> getFollowerCountByUserId(@PathVariable Long userId) {
        FollowerCountDto followerCount = followService.getFollowerCountByUserId(userId);
        return ResponseEntity.ok(followerCount);
    }

    @GetMapping("/following/count/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FollowingCountDto> getFollowingCountByUserId(@PathVariable Long userId) {
        FollowingCountDto followingCount = followService.getFollowingCountByUserId(userId);
        return ResponseEntity.ok(followingCount);
    }
}
