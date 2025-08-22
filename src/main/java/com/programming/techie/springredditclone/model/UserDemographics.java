package com.programming.techie.springredditclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_demographics")
public class UserDemographics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
    
    private Integer age;
    private String gender;
    private String location;
    private String country;
    private String city;
    private String timezone;
    private String language;
    private LocalDate dateOfBirth;
    private String occupation;
    private String education;
    private String maritalStatus;
    private String incomeRange;
} 