package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.GetIntroDto;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.UserIntro;
import com.programming.techie.springredditclone.repository.UserIntroRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserIntroRepository userIntroRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserIntroRepository userIntroRepository) {
        this.userRepository = userRepository;
        this.userIntroRepository = userIntroRepository;
    }

    @Override
    public GetIntroDto getUserIntro(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<UserIntro> userIntroOpt = userIntroRepository.findByUserId(userId);
        
        if (userIntroOpt.isEmpty()) {
            // Return basic user info if no intro exists
            return new GetIntroDto(
                user.getUserId(),
                user.getUsername(),
                null  // bio
            );
        }
        
        UserIntro userIntro = userIntroOpt.get();
        return new GetIntroDto(
            user.getUserId(),
            user.getUsername(),
            userIntro.getBio()
        );
    }
} 