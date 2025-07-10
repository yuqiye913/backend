package com.programming.techie.springredditclone.event;

import com.programming.techie.springredditclone.model.User;
import lombok.Getter;

/**
 * Event fired when a user follows another user
 */
@Getter
public class UserFollowedEvent extends UserActionEvent {
    
    public UserFollowedEvent(Object source, User follower, User followed) {
        super(source, follower, followed, "USER_FOLLOWED");
    }
} 