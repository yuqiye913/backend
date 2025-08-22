package com.programming.techie.springredditclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String username;
    private String password;
    private String email;
    private Instant created;
    private boolean enabled;
    
    // Relationships to new entities
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserDemographics demographics;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPersonality personality;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserSetting settings;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserIntro intro;
    
    // Following relationships
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Follow> following;
    
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Follow> followers;
    
    // Blocking relationships
    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Block> blockedUsers;
    
    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Block> blockedByUsers;
    
    // Phone call relationships
    @OneToMany(mappedBy = "caller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhoneCall> outgoingCalls;
    
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhoneCall> incomingCalls;
    
    // Direct message relationships
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DirectMessage> sentMessages;
    
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DirectMessage> receivedMessages;
    
    // Calling request relationships
    @OneToMany(mappedBy = "caller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CallingRequest> sentCallingRequests;
    
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CallingRequest> receivedCallingRequests;
    
    // Calling response relationships
    @Transient
    @OneToMany(mappedBy = "responder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CallingResponse> callingResponses;
    
    // Match relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matches;
    
    @OneToMany(mappedBy = "matchedUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matchedBy;
}
