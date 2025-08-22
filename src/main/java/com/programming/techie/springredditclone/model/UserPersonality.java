package com.programming.techie.springredditclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_personality")
public class UserPersonality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
    
    private String personalityType; // e.g., INTJ, ENFP, etc.
    private String communicationStyle;
    private String socialPreference; // introvert, extrovert, ambivert
    private String decisionMakingStyle;
    private String conflictResolutionStyle;
    private String learningStyle;
    private String workStyle;
    private String stressResponse;
    private String motivationType;
    
    @ElementCollection
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "personality_id"))
    @Column(name = "interest")
    private List<String> interests;
    
    @ElementCollection
    @CollectionTable(name = "user_hobbies", joinColumns = @JoinColumn(name = "personality_id"))
    @Column(name = "hobby")
    private List<String> hobbies;
    
    @ElementCollection
    @CollectionTable(name = "user_values", joinColumns = @JoinColumn(name = "personality_id"))
    @Column(name = "value")
    private List<String> values;
} 