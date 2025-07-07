package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.Follow;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowing(User following);
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    boolean existsByFollowerAndFollowing(User follower, User following);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.following = :user AND f.isActive = true")
    Long countFollowersByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower = :user AND f.isActive = true")
    Long countFollowingByUser(@Param("user") User user);
    
    @Query("SELECT f FROM Follow f WHERE f.follower = :user AND f.isActive = true")
    List<Follow> findActiveFollowingByUser(@Param("user") User user);
    
    @Query("SELECT f FROM Follow f WHERE f.following = :user AND f.isActive = true")
    List<Follow> findActiveFollowersByUser(@Param("user") User user);
} 