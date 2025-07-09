package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.dto.GetFollowersDto;
import com.programming.techie.springredditclone.dto.FollowerCountDto;
import com.programming.techie.springredditclone.dto.FollowingCountDto;
import com.programming.techie.springredditclone.model.Follow;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.FollowRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    public FollowServiceImpl(FollowRepository followRepository, UserRepository userRepository, AuthService authService) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public void followUser(FollowRequestDto followRequest) {
        // Get the authenticated user as the follower
        User follower = authService.getCurrentUser();
        User following = userRepository.findById(followRequest.getFollowingId())
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("Already following this user");
        }

        if (follower.equals(following)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setActive(true);
        followRepository.save(follow);
    }

    @Override
    public void unfollowUser(Long followingId) {
        // Get the authenticated user as the follower
        User follower = authService.getCurrentUser();
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        if (follower.equals(following)) {
            throw new RuntimeException("You cannot unfollow yourself");
        }

        // Find the existing follow relationship
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowing(follower, following);
        
        if (existingFollow.isEmpty()) {
            throw new RuntimeException("You are not following this user");
        }

        // Delete the follow relationship
        followRepository.delete(existingFollow.get());
    }

    @Override
    public List<GetFollowersDto> getFollowersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Follow> followers = followRepository.findActiveFollowersByUser(user);
        
        return followers.stream()
                .map(this::mapFollowToGetFollowersDto)
                .collect(Collectors.toList());
    }

    @Override
    public FollowerCountDto getFollowerCountByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Long followerCount = followRepository.countFollowersByUser(user);
        
        return new FollowerCountDto(user.getUserId(), user.getUsername(), followerCount);
    }

    @Override
    public FollowingCountDto getFollowingCountByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Long followingCount = followRepository.countFollowingByUser(user);
        
        return new FollowingCountDto(user.getUserId(), user.getUsername(), followingCount);
    }

    private GetFollowersDto mapFollowToGetFollowersDto(Follow follow) {
        User follower = follow.getFollower();
        return new GetFollowersDto(
                follower.getUserId(),
                follower.getUsername(),
                follower.getEmail(),
                follower.getCreated(),
                follower.isEnabled(),
                follow.getFollowedAt(),
                follow.isActive(),
                follow.isMuted(),
                follow.isCloseFriend()
        );
    }
}
