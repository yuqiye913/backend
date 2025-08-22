package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.UserPersonality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPersonalityRepository extends JpaRepository<UserPersonality, Long> {
    @Query("SELECT p FROM UserPersonality p WHERE p.user.userId = :userId")
    Optional<UserPersonality> findByUserId(@Param("userId") Long userId);
} 