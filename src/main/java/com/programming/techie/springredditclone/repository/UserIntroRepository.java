package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.UserIntro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserIntroRepository extends JpaRepository<UserIntro, Long> {
    @Query("SELECT i FROM UserIntro i WHERE i.user.userId = :userId")
    Optional<UserIntro> findByUserId(@Param("userId") Long userId);
    List<UserIntro> findByIsPublic(boolean isPublic);
    List<UserIntro> findByIsVerified(boolean isVerified);
    
    @Query("SELECT ui FROM UserIntro ui WHERE ui.isPublic = true AND (ui.displayName LIKE %:searchTerm% OR ui.bio LIKE %:searchTerm% OR ui.tagline LIKE %:searchTerm%)")
    List<UserIntro> searchPublicProfiles(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT ui FROM UserIntro ui WHERE ui.isPublic = true AND ui.currentCity = :city")
    List<UserIntro> findByCurrentCity(@Param("city") String city);
    
    @Query("SELECT ui FROM UserIntro ui WHERE ui.isPublic = true AND ui.industry = :industry")
    List<UserIntro> findByIndustry(@Param("industry") String industry);
    
    @Query("SELECT ui FROM UserIntro ui WHERE ui.isPublic = true AND ui.relationshipStatus = :status")
    List<UserIntro> findByRelationshipStatus(@Param("status") String status);
    
    @Query("SELECT ui FROM UserIntro ui WHERE ui.isPublic = true AND ui.lookingFor = :lookingFor")
    List<UserIntro> findByLookingFor(@Param("lookingFor") String lookingFor);
} 