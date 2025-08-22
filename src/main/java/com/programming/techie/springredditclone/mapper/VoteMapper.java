package com.programming.techie.springredditclone.mapper;

import com.programming.techie.springredditclone.dto.CommentVoteRequest;
import com.programming.techie.springredditclone.dto.VoteDto;
import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    @Mapping(target = "voteId", ignore = true)
    @Mapping(target = "voteType", source = "voteDto.voteType")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "user", source = "user")
    Vote mapToVote(VoteDto voteDto, Post post, User user);
    
    @Mapping(target = "voteId", ignore = true)
    @Mapping(target = "voteType", source = "commentVoteRequest.voteType")
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "user", source = "user")
    Vote mapToCommentVote(CommentVoteRequest commentVoteRequest, Comment comment, User user);
} 