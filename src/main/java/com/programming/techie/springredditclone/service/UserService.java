package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.GetIntroDto;

public interface UserService {
    GetIntroDto getUserIntro(Long userId);
} 