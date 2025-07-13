package com.programming.techie.springredditclone.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.programming.techie.springredditclone.service.FollowService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.dto.GetFollowersDto;
import com.programming.techie.springredditclone.dto.GetFollowingDto;
import com.programming.techie.springredditclone.dto.FollowerCountDto;
import com.programming.techie.springredditclone.dto.FollowingCountDto;
import com.programming.techie.springredditclone.dto.FollowStatusDto;
import com.programming.techie.springredditclone.dto.FollowResponseDto;
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
    public ResponseEntity<FollowResponseDto> followUser(@Valid @RequestBody FollowRequestDto followRequest) {
        followService.followUser(followRequest);
        FollowResponseDto response = new FollowResponseDto();
        response.setMessage("Successfully followed user");
        response.setSuccess(true);
        response.setFollowingId(followRequest.getFollowingId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/follow/{followingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FollowResponseDto> unfollowUser(@PathVariable Long followingId) {
        followService.unfollowUser(followingId);
        FollowResponseDto response = new FollowResponseDto();
        response.setMessage("Successfully unfollowed user");
        response.setSuccess(true);
        response.setFollowingId(followingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/followers/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GetFollowersDto>> getFollowersByUserId(@PathVariable Long userId) {
        List<GetFollowersDto> followers = followService.getFollowersByUserId(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GetFollowingDto>> getFollowingByUserId(@PathVariable Long userId) {
        List<GetFollowingDto> following = followService.getFollowingByUserId(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followers/count/{userId}")
    public ResponseEntity<FollowerCountDto> getFollowerCountByUserId(@PathVariable Long userId) {
        FollowerCountDto followerCount = followService.getFollowerCountByUserId(userId);
        return ResponseEntity.ok(followerCount);
    }

    @GetMapping("/following/count/{userId}")
    public ResponseEntity<FollowingCountDto> getFollowingCountByUserId(@PathVariable Long userId) {
        FollowingCountDto followingCount = followService.getFollowingCountByUserId(userId);
        return ResponseEntity.ok(followingCount);
    }
    
    @GetMapping("/is-following/{followingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FollowStatusDto> isFollowingUser(@PathVariable Long followingId) {
        boolean isFollowing = followService.isFollowingUser(followingId);
        FollowStatusDto response = new FollowStatusDto();
        response.setUserId(followingId);
        response.setFollowing(isFollowing);
        response.setMessage(isFollowing ? "User is following" : "User is not following");
        return ResponseEntity.ok(response);
    }
}
