package com.programming.techie.springredditclone.event;

import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import lombok.Getter;

/**
 * Event fired when a user comments on a post
 */
@Getter
public class PostCommentedEvent extends UserActionEvent {
    
    private final Post post;
    private final Comment comment;
    private final Long postId;
    private final Long commentId;
    
    public PostCommentedEvent(Object source, User commenter, User postOwner, Post post, Comment comment) {
        super(source, commenter, postOwner, "POST_COMMENTED");
        this.post = post;
        this.comment = comment;
        this.postId = post.getPostId();
        this.commentId = comment.getId();
    }
} 