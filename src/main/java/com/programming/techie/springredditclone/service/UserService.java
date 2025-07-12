package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.GetIntroDto;

import java.util.List;

public interface UserService {
    GetIntroDto getUserIntro(Long userId);
    List<GetIntroDto> searchUsersByUsername(String username);
} 