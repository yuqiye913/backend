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
@Table(name = "user_intros")
public class UserIntro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
    
    // Basic Introduction
    private String displayName;
    private String bio;
    private String tagline;
    private String profilePictureUrl;
    private String coverPhotoUrl;
    
    // Personal Information
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickname;
    private String pronouns; // he/him, she/her, they/them, etc.
    private String relationshipStatus;
    private String lookingFor; // friendship, relationship, networking, etc.
    
    // Contact Information
    private String phoneNumber;
    private String website;
    private String linkedinUrl;
    private String twitterHandle;
    private String instagramHandle;
    private String facebookUrl;
    
    // Location Information
    private String currentCity;
    private String hometown;
    private String country;
    private String timezone;
    private boolean showLocation;
    
    // Professional Information
    private String jobTitle;
    private String company;
    private String industry;
    private String skills;
    private String education;
    private String certifications;
    
    // Personal Details
    private String birthDate;
    private String zodiacSign;
    private String bloodType;
    private String height;
    private String weight;
    private String bodyType;
    private String ethnicity;
    private String religion;
    private String politicalViews;
    
    // Interests and Preferences
    @ElementCollection
    @CollectionTable(name = "user_interests_intro", joinColumns = @JoinColumn(name = "intro_id"))
    @Column(name = "interest")
    private List<String> interests;
    
    @ElementCollection
    @CollectionTable(name = "user_hobbies_intro", joinColumns = @JoinColumn(name = "intro_id"))
    @Column(name = "hobby")
    private List<String> hobbies;
    
    @ElementCollection
    @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "intro_id"))
    @Column(name = "language")
    private List<String> languages;
    
    // Social Preferences
    private String socialStyle; // outgoing, reserved, balanced
    private String communicationPreference; // text, voice, video, in-person
    private String meetingPreference; // coffee, dinner, activity, virtual
    private String availability; // weekday evenings, weekends, flexible
    
    // Additional Information
    private String aboutMe;
    private String whatImLookingFor;
    private String dealBreakers;
    private String funFacts;
    private String favoriteQuotes;
    private String lifeGoals;
    
    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isPublic;
    private boolean isVerified;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        isPublic = true;
        isVerified = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
} 