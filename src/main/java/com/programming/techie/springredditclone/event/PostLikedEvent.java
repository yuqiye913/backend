package com.programming.techie.springredditclone.event;

import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import lombok.Getter;

/**
 * Event fired when a user likes/upvotes a post
 */
@Getter
public class PostLikedEvent extends UserActionEvent {
    
    private final Post post;
    private final Long postId;
    
    public PostLikedEvent(Object source, User liker, User postOwner, Post post) {
        super(source, liker, postOwner, "POST_LIKED");
        this.post = post;
        this.postId = post.getPostId();
    }
} 