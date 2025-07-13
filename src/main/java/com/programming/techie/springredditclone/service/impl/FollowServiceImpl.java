package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.dto.GetFollowersDto;
import com.programming.techie.springredditclone.dto.GetFollowingDto;
import com.programming.techie.springredditclone.dto.FollowerCountDto;
import com.programming.techie.springredditclone.dto.FollowingCountDto;
import com.programming.techie.springredditclone.event.UserFollowedEvent;
import com.programming.techie.springredditclone.exceptions.NotFollowingException;
import com.programming.techie.springredditclone.model.Follow;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.FollowRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.FollowService;
import com.programming.techie.springredditclone.mapper.FollowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final BlockService blockService;
    private final FollowMapper followMapper;
    private final ApplicationEventPublisher eventPublisher;

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

        // Check if users are blocked
        if (blockService.hasBlockedUser(following.getUserId()) || blockService.isBlockedByUser(following.getUserId())) {
            throw new RuntimeException("Cannot follow user due to block restrictions");
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setActive(true);
        followRepository.save(follow);
        
        // Publish follow event for notification
        eventPublisher.publishEvent(new UserFollowedEvent(this, follower, following));
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
            throw new NotFollowingException("You are not following this user");
        }

        // Delete the follow relationship
        followRepository.delete(existingFollow.get());
    }

    @Override
    public List<GetFollowersDto> getFollowersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User currentUser = authService.getCurrentUser();
        
        // Check if current user is blocked by target user or has blocked target user
        if (blockService.isBlockedByUser(userId) || blockService.hasBlockedUser(userId)) {
            throw new RuntimeException("Cannot view followers due to block restrictions");
        }
        
        List<Follow> followers = followRepository.findActiveFollowersByUser(user);
        
        return followers.stream()
                .map(followMapper::mapToGetFollowersDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetFollowingDto> getFollowingByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User currentUser = authService.getCurrentUser();
        
        // Check if current user is blocked by target user or has blocked target user
        if (blockService.isBlockedByUser(userId) || blockService.hasBlockedUser(userId)) {
            throw new RuntimeException("Cannot view following due to block restrictions");
        }
        
        List<Follow> following = followRepository.findActiveFollowingByUser(user);
        
        return following.stream()
                .map(followMapper::mapToGetFollowingDto)
                .collect(Collectors.toList());
    }

    @Override
    public FollowerCountDto getFollowerCountByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Long followerCount = followRepository.countFollowersByUser(user);
        
        return followMapper.mapToFollowerCountDto(user, followerCount);
    }

    @Override
    public FollowingCountDto getFollowingCountByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Long followingCount = followRepository.countFollowingByUser(user);
        
        return followMapper.mapToFollowingCountDto(user, followingCount);
    }

    @Override
    public boolean isFollowingUser(Long followingId) {
        User follower = authService.getCurrentUser();
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

}
