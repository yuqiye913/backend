package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.FriendRequest;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findBySender(User sender);
    List<FriendRequest> findByReceiver(User receiver);
    List<FriendRequest> findByStatus(String status);
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    boolean existsBySenderAndReceiver(User sender, User receiver);
    
    @Query("SELECT fr FROM FriendRequest fr WHERE fr.receiver = :user AND fr.status = 'pending' ORDER BY fr.requestedAt DESC")
    List<FriendRequest> findPendingRequestsByReceiver(@Param("user") User user);
    
    @Query("SELECT fr FROM FriendRequest fr WHERE fr.sender = :user AND fr.status = 'pending' ORDER BY fr.requestedAt DESC")
    List<FriendRequest> findPendingRequestsBySender(@Param("user") User user);
    
    @Query("SELECT COUNT(fr) FROM FriendRequest fr WHERE fr.receiver = :user AND fr.status = 'pending'")
    Long countPendingRequestsByReceiver(@Param("user") User user);
    
    @Query("SELECT fr FROM FriendRequest fr WHERE (fr.sender = :user OR fr.receiver = :user) AND fr.status = 'accepted' ORDER BY fr.requestedAt DESC")
    List<FriendRequest> findAcceptedRequestsByUser(@Param("user") User user);
} 