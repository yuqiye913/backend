package com.programming.techie.springredditclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long voteId;
    
    @Enumerated(EnumType.STRING)
    private VoteType voteType;
    
    // Vote can be on either a post or a comment (mutually exclusive)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "postId")
    private Post post;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "commentId", referencedColumnName = "id")
    private Comment comment;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;
    
    // Ensure vote is on either post or comment, not both
    @PrePersist
    @PreUpdate
    protected void validateVoteTarget() {
        if ((post == null && comment == null) || (post != null && comment != null)) {
            throw new IllegalStateException("Vote must be on either a post or a comment, not both or neither");
        }
    }
}
