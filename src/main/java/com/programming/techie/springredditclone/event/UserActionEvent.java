package com.programming.techie.springredditclone.event;

import com.programming.techie.springredditclone.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Base event class for user actions that can trigger notifications
 */
@Getter
public abstract class UserActionEvent extends ApplicationEvent {
    
    private final User actor;
    private final User recipient;
    private final String actionType;
    
    protected UserActionEvent(Object source, User actor, User recipient, String actionType) {
        super(source);
        this.actor = actor;
        this.recipient = recipient;
        this.actionType = actionType;
    }
} 