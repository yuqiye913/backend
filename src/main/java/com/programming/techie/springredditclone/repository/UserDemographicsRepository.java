package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.UserDemographics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDemographicsRepository extends JpaRepository<UserDemographics, Long> {
    Optional<UserDemographics> findByUserId(Long userId);
} 