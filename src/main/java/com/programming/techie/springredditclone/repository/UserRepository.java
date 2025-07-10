package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    @Query(value = "SELECT * FROM users u WHERE u.user_id != :currentUserId AND u.enabled = true ORDER BY u.created DESC LIMIT :limit", nativeQuery = true)
    List<User> findPotentialMatches(@Param("currentUserId") Long currentUserId, @Param("limit") int limit);
}
