package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.dto.GetFollowersDto;
import com.programming.techie.springredditclone.dto.GetFollowingDto;
import com.programming.techie.springredditclone.dto.FollowerCountDto;
import com.programming.techie.springredditclone.dto.FollowingCountDto;

import java.util.List;

public interface FollowService {
    void followUser(FollowRequestDto followRequest);
    void unfollowUser(Long followingId);
    List<GetFollowersDto> getFollowersByUserId(Long userId);
    List<GetFollowingDto> getFollowingByUserId(Long userId);
    FollowerCountDto getFollowerCountByUserId(Long userId);
    FollowingCountDto getFollowingCountByUserId(Long userId);
}
