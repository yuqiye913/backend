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
    private Instant createdDate;
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
    private Instant editedDate;
    private boolean isDeleted;
    private Instant deletedDate;
    private String deletedBy;
    
    // Comment status
    private boolean isHidden;
    private String hiddenReason;
    private String hiddenBy;
    private Instant hiddenDate;
    
    // User info
    private String userDisplayName;
    private String userProfilePicture;
}
