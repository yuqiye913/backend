package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentsDto {
    private Long id;
    @NotNull(message = "Post ID is required")
    private Long postId;
    private Long createdDate;
    @NotBlank(message = "Comment text is required")
    private String text;
    private String userName;
    
    // Threading/replies support
    private Long parentCommentId;
    private List<CommentsDto> replies;
    private Integer replyCount;
    
    // Comment metadata
    private Integer voteCount;
    private boolean isEdited;
    private Long editedDate;
    private boolean isDeleted;
    private Long deletedDate;
    private String deletedBy;
    
    // Comment status
    private boolean isHidden;
    private String hiddenReason;
    private String hiddenBy;
    private Long hiddenDate;
    
    // User info
    private String userDisplayName;
    private String userProfilePicture;

    // Indicates if the current user liked this comment
    private boolean upVote;
}
