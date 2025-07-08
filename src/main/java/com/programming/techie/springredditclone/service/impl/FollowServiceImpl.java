package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.model.Follow;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.FollowRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
