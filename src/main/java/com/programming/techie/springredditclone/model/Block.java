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
@Table(name = "blocks")
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // block_id
    
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", referencedColumnName = "userId")
    private User blocker; // User who is blocking
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", referencedColumnName = "userId")
    private User blocked; // User who is being blocked
    
    private Instant blockedAt;
    private String reason;
    private boolean isActive;
    
    @PrePersist
    protected void onCreate() {
        blockedAt = Instant.now();
        isActive = true;
    }
} 