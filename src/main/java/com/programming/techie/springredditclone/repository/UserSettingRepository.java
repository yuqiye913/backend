package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
    Optional<UserSetting> findByUserId(Long userId);
} 