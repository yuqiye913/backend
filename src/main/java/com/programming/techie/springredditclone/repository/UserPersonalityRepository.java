package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.UserPersonality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPersonalityRepository extends JpaRepository<UserPersonality, Long> {
    Optional<UserPersonality> findByUserId(Long userId);
} 