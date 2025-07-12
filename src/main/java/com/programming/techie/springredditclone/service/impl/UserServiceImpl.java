package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.GetIntroDto;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.UserIntro;
import com.programming.techie.springredditclone.repository.UserIntroRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.UserService;
import com.programming.techie.springredditclone.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserIntroRepository userIntroRepository;
    private final AuthService authService;
    private final BlockService blockService;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserIntroRepository userIntroRepository, 
                          AuthService authService, BlockService blockService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userIntroRepository = userIntroRepository;
        this.authService = authService;
        this.blockService = blockService;
        this.userMapper = userMapper;
    }

    @Override
    public GetIntroDto getUserIntro(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User currentUser = authService.getCurrentUser();
        
        // Check if current user is blocked by target user or has blocked target user
        if (blockService.isBlockedByUser(userId) || blockService.hasBlockedUser(userId)) {
            throw new RuntimeException("Cannot view user profile due to block restrictions");
        }
        
        Optional<UserIntro> userIntroOpt = userIntroRepository.findByUserId(userId);
        
        if (userIntroOpt.isEmpty()) {
            // Return basic user info if no intro exists
            return userMapper.mapToGetIntroDto(user);
        }
        
        UserIntro userIntro = userIntroOpt.get();
        return userMapper.mapToGetIntroDto(user, userIntro);
    }

    @Override
    public List<GetIntroDto> searchUsersByUsername(String username) {
        // Search for users whose username contains the search term (case-insensitive)
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .toList();
        
        return users.stream()
                .map(user -> {
                    Optional<UserIntro> userIntroOpt = userIntroRepository.findByUserId(user.getUserId());
                    if (userIntroOpt.isPresent()) {
                        return userMapper.mapToGetIntroDto(user, userIntroOpt.get());
                    } else {
                        return userMapper.mapToGetIntroDto(user);
                    }
                })
                .toList();
    }
} 