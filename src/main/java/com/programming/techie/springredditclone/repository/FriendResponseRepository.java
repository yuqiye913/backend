package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.FriendResponse;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendResponseRepository extends JpaRepository<FriendResponse, Long> {
    List<FriendResponse> findByResponder(User responder);
    List<FriendResponse> findByResponseStatus(String responseStatus);
    Optional<FriendResponse> findByRequestId(Long requestId);
    
    @Query("SELECT fr FROM FriendResponse fr WHERE fr.responder = :user ORDER BY fr.respondedAt DESC")
    List<FriendResponse> findResponsesByResponder(@Param("user") User user);
    
    @Query("SELECT fr FROM FriendResponse fr WHERE fr.responseStatus = 'accepted' AND fr.responder = :user ORDER BY fr.respondedAt DESC")
    List<FriendResponse> findAcceptedResponsesByResponder(@Param("user") User user);
    
    @Query("SELECT COUNT(fr) FROM FriendResponse fr WHERE fr.responder = :user AND fr.isRead = false")
    Long countUnreadResponsesByResponder(@Param("user") User user);
} 