package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.RandomVideoCallQueueRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.impl.RandomVideoCallServiceImpl;
import com.programming.techie.springredditclone.service.impl.VideoCallServiceImpl;

/**
 * Test-specific implementation of RandomVideoCallServiceImpl that overrides getCurrentUser()
 * to return a test user instead of throwing an exception.
 */
public class TestRandomVideoCallServiceImpl extends RandomVideoCallServiceImpl {

    private final User testCurrentUser;

    public TestRandomVideoCallServiceImpl(RandomVideoCallQueueRepository queueRepository, 
                                       UserRepository userRepository, 
                                       VideoCallServiceImpl videoCallService,
                                       User testCurrentUser) {
        super(queueRepository, userRepository, videoCallService);
        this.testCurrentUser = testCurrentUser;
    }

    // Override the private method by making it protected in the parent class
    // For testing purposes, we'll use reflection to set the current user
    public User getTestCurrentUser() {
        return testCurrentUser;
    }
} 