package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.FollowRequestDto;

public interface FollowService {
    void followUser(FollowRequestDto followRequest);
}
