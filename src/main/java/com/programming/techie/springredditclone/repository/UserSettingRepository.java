package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
    @Query("SELECT s FROM UserSetting s WHERE s.user.userId = :userId")
    Optional<UserSetting> findByUserId(@Param("userId") Long userId);
} 