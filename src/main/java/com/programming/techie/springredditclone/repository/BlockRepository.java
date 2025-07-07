package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.Block;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByBlocker(User blocker);
    List<Block> findByBlocked(User blocked);
    Optional<Block> findByBlockerAndBlocked(User blocker, User blocked);
    boolean existsByBlockerAndBlocked(User blocker, User blocked);
    
    @Query("SELECT b FROM Block b WHERE b.blocker = :user OR b.blocked = :user")
    List<Block> findAllBlocksInvolvingUser(@Param("user") User user);
} 