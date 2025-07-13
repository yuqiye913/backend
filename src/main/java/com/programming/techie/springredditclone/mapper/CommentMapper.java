package com.programming.techie.springredditclone.mapper;

import com.programming.techie.springredditclone.dto.CommentsDto;
import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    
    // Map from CommentsDto to Comment (for backward compatibility)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentsDto.text")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "editedDate", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    @Mapping(target = "hiddenDate", ignore = true)
    @Mapping(target = "post", source = "post")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "voteCount", constant = "0")
    @Mapping(target = "replyCount", constant = "0")
    @Mapping(target = "edited", constant = "false")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "hidden", constant = "false")
    Comment map(CommentsDto commentsDto, Post post, User user);

    // Map from CreateCommentRequest to Comment
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "request.text")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "parentComment", source = "parentComment")
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "voteCount", constant = "0")
    @Mapping(target = "replyCount", constant = "0")
    @Mapping(target = "edited", constant = "false")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "hidden", constant = "false")
    Comment mapToComment(com.programming.techie.springredditclone.dto.CreateCommentRequest request, Post post, User user, Comment parentComment);

    // Map Comment to CommentsDto (basic mapping without replies)
    @Mapping(target = "postId", expression = "java(comment.getPost().getPostId())")
    @Mapping(target = "userName", expression = "java(comment.getUser().getUsername())")
    @Mapping(target = "parentCommentId", expression = "java(comment.getParentComment() != null ? comment.getParentComment().getId() : null)")
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "replyCount", expression = "java(comment.getReplyCount() != null ? comment.getReplyCount() : 0)")
    @Mapping(target = "userDisplayName", expression = "java(comment.getUser().getUsername())")
    @Mapping(target = "userProfilePicture", constant = "")
    @Mapping(target = "createdDate", expression = "java(comment.getCreatedDate() != null ? comment.getCreatedDate().toEpochMilli() : null)")
    @Mapping(target = "editedDate", expression = "java(comment.getEditedDate() != null ? comment.getEditedDate().toEpochMilli() : null)")
    @Mapping(target = "deletedDate", expression = "java(comment.getDeletedDate() != null ? comment.getDeletedDate().toEpochMilli() : null)")
    @Mapping(target = "hiddenDate", expression = "java(comment.getHiddenDate() != null ? comment.getHiddenDate().toEpochMilli() : null)")
    CommentsDto mapToDto(Comment comment);

    // Map list of comments to DTOs (without replies to avoid ambiguity)
    List<CommentsDto> mapToDtoList(List<Comment> comments);
}
