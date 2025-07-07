package com.programming.techie.springredditclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "follows")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", referencedColumnName = "userId")
    private User follower; // User who is following
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", referencedColumnName = "userId")
    private User following; // User who is being followed
    
    private Instant followedAt;
    private boolean isActive;
    private boolean isMuted; // Mute posts from this user
    private boolean isCloseFriend; // Close friend status
    
    @PrePersist
    protected void onCreate() {
        followedAt = Instant.now();
        isActive = true;
        isMuted = false;
        isCloseFriend = false;
    }
} 