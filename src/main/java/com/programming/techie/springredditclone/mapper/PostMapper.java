package com.programming.techie.springredditclone.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.dto.PostResponse;
import com.programming.techie.springredditclone.model.*;
import com.programming.techie.springredditclone.repository.CommentRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;



@Mapper(componentModel = "spring")
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository;

    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddits", expression = "java(mapSubreddits(postRequest.getSubredditNames()))")
    @Mapping(target = "voteCount", constant = "0")
    @Mapping(target = "user", source = "user")
    public abstract Post map(PostRequest postRequest, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditNames", expression = "java(getSubredditNames(post))")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    public abstract PostResponse mapToDto(Post post);

    // Helper method to map subreddit names to subreddit entities
    protected Set<Subreddit> mapSubreddits(List<String> subredditNames) {
        // This will be implemented in the service layer
        return new HashSet<>();
    }

    // Helper method to get subreddit names from post
    protected List<String> getSubredditNames(Post post) {
        if (post.getSubreddits() != null) {
            return post.getSubreddits().stream()
                    .map(Subreddit::getName)
                    .toList();
        }
        return List.of();
    }

    Integer commentCount(Post post) {
        return post.getCommentCount();
    }

    String getDuration(Post post) {
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }


}