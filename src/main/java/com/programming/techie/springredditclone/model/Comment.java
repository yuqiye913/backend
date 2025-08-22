package com.programming.techie.springredditclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String text;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "postId")
    private Post post;
    
    private Instant createdDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;
    
    // Comment threading/replies support
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCommentId", referencedColumnName = "id")
    private Comment parentComment;
    
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> replies;
    
    // Comment metadata
    private Integer voteCount = 0;
    private Integer replyCount = 0;
    private boolean isEdited = false;
    private Instant editedDate;
    private boolean deleted = false;
    private Instant deletedDate;
    private String deletedBy; // username of who deleted it
    
    // Comment status
    private boolean isHidden = false;
    private String hiddenReason;
    private String hiddenBy; // username of who hidden it
    private Instant hiddenDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = Instant.now();
        voteCount = 0;
        replyCount = 0;
        isEdited = false;
        isHidden = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        if (isEdited) {
            editedDate = Instant.now();
        }
    }
}
