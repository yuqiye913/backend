package com.programming.techie.springredditclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_settings")
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
    
    // Privacy Settings
    private boolean profilePublic;
    private boolean showOnlineStatus;
    private boolean allowDirectMessages;
    private boolean allowPhoneCalls;
    private boolean allowFollowRequests;
    private boolean showLastSeen;
    private boolean showReadReceipts;
    
    // Notification Settings
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean smsNotifications;
    private boolean newFollowerNotifications;
    private boolean messageNotifications;
    private boolean callNotifications;
    private boolean postNotifications;
    private boolean commentNotifications;
    
    // Display Settings
    private String theme; // light, dark, auto
    private String language;
    private String timezone;
    private boolean autoPlayVideos;
    private boolean showSensitiveContent;
    private String fontSize; // small, medium, large
    
    // Security Settings
    private boolean twoFactorEnabled;
    private boolean loginAlerts;
    private boolean deviceManagement;
    private String sessionTimeout; // in minutes
    
    // Content Settings
    private boolean autoSaveDrafts;
    private boolean showTrendingTopics;
    private boolean personalizedAds;
    private String contentFilterLevel; // strict, moderate, lenient
    
    @ElementCollection
    @CollectionTable(name = "user_custom_settings", joinColumns = @JoinColumn(name = "setting_id"))
    @MapKeyColumn(name = "setting_key")
    @Column(name = "setting_value")
    private Map<String, String> customSettings;
} 