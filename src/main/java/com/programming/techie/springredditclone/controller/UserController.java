package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.GetIntroDto;
import com.programming.techie.springredditclone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/intro/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetIntroDto> getUserIntro(@PathVariable Long userId) {
        GetIntroDto userIntro = userService.getUserIntro(userId);
        return ResponseEntity.ok(userIntro);
    }
} 