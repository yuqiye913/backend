package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.UserDemographics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDemographicsRepository extends JpaRepository<UserDemographics, Long> {
    @Query("SELECT d FROM UserDemographics d WHERE d.user.userId = :userId")
    Optional<UserDemographics> findByUserId(@Param("userId") Long userId);
} 