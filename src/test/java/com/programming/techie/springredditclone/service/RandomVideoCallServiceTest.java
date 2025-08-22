package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.RandomVideoCallRequestDto;
import com.programming.techie.springredditclone.dto.RandomVideoCallResponseDto;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.RandomVideoCallQueue;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.RandomVideoCallQueueRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.impl.RandomVideoCallServiceImpl;
import com.programming.techie.springredditclone.service.impl.VideoCallServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RandomVideoCallServiceTest {

    @Mock
    private RandomVideoCallQueueRepository queueRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VideoCallServiceImpl videoCallService;

    @InjectMocks
    private RandomVideoCallServiceImpl randomVideoCallService;

    private User user1;
    private User user2;
    private RandomVideoCallQueue queueEntry1;
    private RandomVideoCallQueue queueEntry2;
    private RandomVideoCallRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Setup test users
        user1 = new User();
        user1.setUserId(1L);
        user1.setUsername("aa");
        user1.setEmail("aa@test.com");
        user1.setEnabled(true);

        user2 = new User();
        user2.setUserId(2L);
        user2.setUsername("bb");
        user2.setEmail("bb@test.com");
        user2.setEnabled(true);

        // Setup queue entries
        queueEntry1 = new RandomVideoCallQueue();
        queueEntry1.setRequestId("req_1");
        queueEntry1.setUser(user1);
        queueEntry1.setQueueStatus("waiting");
        queueEntry1.setQueuePosition(1L);
        queueEntry1.setPreferredGender("any");
        queueEntry1.setPreferredAgeRange("any");
        queueEntry1.setPreferredLanguage("en");
        queueEntry1.setRequestCreatedAt(Instant.now());

        queueEntry2 = new RandomVideoCallQueue();
        queueEntry2.setRequestId("req_2");
        queueEntry2.setUser(user2);
        queueEntry2.setQueueStatus("waiting");
        queueEntry2.setQueuePosition(2L);
        queueEntry2.setPreferredGender("any");
        queueEntry2.setPreferredAgeRange("any");
        queueEntry2.setPreferredLanguage("en");
        queueEntry2.setRequestCreatedAt(Instant.now());

        // Setup request DTO
        requestDto = new RandomVideoCallRequestDto();
        requestDto.setUserId(1L);
        requestDto.setCallType("video");
        requestDto.setCallPurpose("casual");
        requestDto.setEnableVideo(true);
        requestDto.setEnableAudio(true);
        requestDto.setVideoQuality("high");
        requestDto.setAudioQuality("high");
        requestDto.setPreferredGender("any");
        requestDto.setPreferredAgeRange("any");
        requestDto.setPreferredLanguage("en");
        requestDto.setPreferredLocation("any");
        requestDto.setPriority(false);
        requestDto.setQueueType("random");
        requestDto.setMaxWaitTime(300L);
    }

    @Test
    void requestRandomVideoCall_ShouldCreateNewRequest() {
        // Arrange
        when(userRepository.findById(3L)).thenReturn(Optional.of(user1));
        when(queueRepository.findActiveRequestByUser(user1)).thenReturn(Optional.empty());
        when(queueRepository.countWaitingUsers()).thenReturn(0L);
        when(queueRepository.save(any(RandomVideoCallQueue.class))).thenReturn(queueEntry1);

        // Act
        RandomVideoCallResponseDto response = randomVideoCallService.requestRandomVideoCall(requestDto);

        // Assert
        assertNotNull(response);
        assertEquals("waiting", response.getQueueStatus());
        assertEquals(1L, response.getQueuePosition());
        verify(queueRepository).save(any(RandomVideoCallQueue.class));
    }

    @Test
    void requestRandomVideoCall_ShouldThrowException_WhenUserAlreadyHasActiveRequest() {
        // Arrange
        when(userRepository.findById(3L)).thenReturn(Optional.of(user1));
        when(queueRepository.findActiveRequestByUser(user1)).thenReturn(Optional.of(queueEntry1));

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> 
            randomVideoCallService.requestRandomVideoCall(requestDto));
    }

    @Test
    void checkQueueStatus_ShouldReturnUpdatedStatus() {
        // Arrange
        when(queueRepository.findByRequestId("req_1")).thenReturn(Optional.of(queueEntry1));
        when(queueRepository.countWaitingUsers()).thenReturn(2L);

        // Act
        RandomVideoCallResponseDto response = randomVideoCallService.checkQueueStatus("req_1");

        // Assert
        assertNotNull(response);
        assertEquals("waiting", response.getQueueStatus());
        verify(queueRepository).save(queueEntry1);
    }

    @Test
    void checkQueueStatus_ShouldThrowException_WhenRequestNotFound() {
        // Arrange
        when(queueRepository.findByRequestId("invalid_id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> 
            randomVideoCallService.checkQueueStatus("invalid_id"));
    }

    @Test
    void processQueueMatching_ShouldMatchTwoCompatibleUsers() {
        // Arrange
        List<RandomVideoCallQueue> waitingUsers = Arrays.asList(queueEntry1, queueEntry2);
        when(queueRepository.findAllWaitingUsers()).thenReturn(waitingUsers);
        when(queueRepository.findPriorityWaitingUsers()).thenReturn(Arrays.asList());
        when(queueRepository.findTimedOutRequests(any(Instant.class))).thenReturn(Arrays.asList());
        when(queueRepository.save(any(RandomVideoCallQueue.class))).thenReturn(queueEntry1);

        // Act
        int matchesMade = randomVideoCallService.processQueueMatching();

        // Assert
        assertEquals(1, matchesMade);
        verify(queueRepository, times(2)).save(any(RandomVideoCallQueue.class));
    }

    @Test
    void processQueueMatching_ShouldNotMatch_WhenLessThanTwoUsers() {
        // Arrange
        List<RandomVideoCallQueue> waitingUsers = Arrays.asList(queueEntry1);
        when(queueRepository.findAllWaitingUsers()).thenReturn(waitingUsers);

        // Act
        int matchesMade = randomVideoCallService.processQueueMatching();

        // Assert
        assertEquals(0, matchesMade);
        verify(queueRepository, never()).save(any(RandomVideoCallQueue.class));
    }

    @Test
    void processQueueMatching_ShouldNotMatch_WhenUsersAreIncompatible() {
        // Arrange
        queueEntry1.setPreferredGender("male");
        queueEntry2.setPreferredGender("female");
        
        List<RandomVideoCallQueue> waitingUsers = Arrays.asList(queueEntry1, queueEntry2);
        when(queueRepository.findAllWaitingUsers()).thenReturn(waitingUsers);
        when(queueRepository.findPriorityWaitingUsers()).thenReturn(Arrays.asList());
        when(queueRepository.findTimedOutRequests(any(Instant.class))).thenReturn(Arrays.asList());

        // Act
        int matchesMade = randomVideoCallService.processQueueMatching();

        // Assert
        assertEquals(0, matchesMade);
        verify(queueRepository, never()).save(any(RandomVideoCallQueue.class));
    }

    @Test
    void processQueueMatching_ShouldNotMatch_WhenUsersAreSame() {
        // Arrange
        queueEntry2.setUser(user1); // Same user
        
        List<RandomVideoCallQueue> waitingUsers = Arrays.asList(queueEntry1, queueEntry2);
        when(queueRepository.findAllWaitingUsers()).thenReturn(waitingUsers);
        when(queueRepository.findPriorityWaitingUsers()).thenReturn(Arrays.asList());
        when(queueRepository.findTimedOutRequests(any(Instant.class))).thenReturn(Arrays.asList());

        // Act
        int matchesMade = randomVideoCallService.processQueueMatching();

        // Assert
        assertEquals(0, matchesMade);
        verify(queueRepository, never()).save(any(RandomVideoCallQueue.class));
    }

    @Test
    void processQueueMatching_ShouldNotMatch_WhenUsersNotWaiting() {
        // Arrange
        queueEntry1.setQueueStatus("matched");
        
        List<RandomVideoCallQueue> waitingUsers = Arrays.asList(queueEntry1, queueEntry2);
        when(queueRepository.findAllWaitingUsers()).thenReturn(waitingUsers);
        when(queueRepository.findPriorityWaitingUsers()).thenReturn(Arrays.asList());
        when(queueRepository.findTimedOutRequests(any(Instant.class))).thenReturn(Arrays.asList());

        // Act
        int matchesMade = randomVideoCallService.processQueueMatching();

        // Assert
        assertEquals(0, matchesMade);
        verify(queueRepository, never()).save(any(RandomVideoCallQueue.class));
    }

    @Test
    void processQueueMatching_ShouldProcessPriorityUsersFirst() {
        // Arrange
        queueEntry1.setIsPriority(true);
        queueEntry2.setIsPriority(true);
        
        List<RandomVideoCallQueue> allUsers = Arrays.asList(queueEntry1, queueEntry2);
        List<RandomVideoCallQueue> priorityUsers = Arrays.asList(queueEntry1, queueEntry2);
        List<RandomVideoCallQueue> regularUsers = Arrays.asList();
        
        when(queueRepository.findAllWaitingUsers()).thenReturn(allUsers);
        when(queueRepository.findPriorityWaitingUsers()).thenReturn(priorityUsers);
        when(queueRepository.findTimedOutRequests(any(Instant.class))).thenReturn(Arrays.asList());
        when(queueRepository.save(any(RandomVideoCallQueue.class))).thenReturn(queueEntry1);

        // Act
        int matchesMade = randomVideoCallService.processQueueMatching();

        // Assert
        assertEquals(1, matchesMade);
        verify(queueRepository, times(2)).save(any(RandomVideoCallQueue.class));
    }

    @Test
    void processQueueMatching_ShouldProcessTimedOutRequests() {
        // Arrange
        List<RandomVideoCallQueue> waitingUsers = Arrays.asList(queueEntry2); // Only one user left after timeout
        List<RandomVideoCallQueue> timedOutRequests = Arrays.asList(queueEntry1);
        
        when(queueRepository.findAllWaitingUsers()).thenReturn(waitingUsers);
        when(queueRepository.findPriorityWaitingUsers()).thenReturn(Arrays.asList());
        when(queueRepository.findTimedOutRequests(any(Instant.class))).thenReturn(timedOutRequests);
        when(queueRepository.save(any(RandomVideoCallQueue.class))).thenReturn(queueEntry1);

        // Act
        int matchesMade = randomVideoCallService.processQueueMatching();

        // Assert
        assertEquals(0, matchesMade);
        // The timeout processing happens after matching, so we verify the timeout was processed
        assertEquals("timeout", queueEntry1.getQueueStatus());
        verify(queueRepository, atLeastOnce()).save(any(RandomVideoCallQueue.class));
    }

    @Test
    void cancelRandomVideoCall_ShouldCancelRequest() {
        // Arrange
        when(userRepository.findById(3L)).thenReturn(Optional.of(user1));
        when(queueRepository.findByRequestId("req_1")).thenReturn(Optional.of(queueEntry1));

        // Act
        randomVideoCallService.cancelRandomVideoCall("req_1");

        // Assert
        assertEquals("cancelled", queueEntry1.getQueueStatus());
        verify(queueRepository).save(queueEntry1);
    }

    @Test
    void acceptMatchedCall_ShouldAcceptCall() {
        // Arrange
        queueEntry1.setQueueStatus("matched");
        when(userRepository.findById(3L)).thenReturn(Optional.of(user1));
        when(queueRepository.findByRequestId("req_1")).thenReturn(Optional.of(queueEntry1));

        // Act
        RandomVideoCallResponseDto response = randomVideoCallService.acceptMatchedCall("req_1");

        // Assert
        assertEquals("connected", queueEntry1.getQueueStatus());
        assertNotNull(queueEntry1.getCallStartedAt());
        verify(queueRepository).save(queueEntry1);
    }

    @Test
    void acceptMatchedCall_ShouldThrowException_WhenNotMatched() {
        // Arrange
        when(queueRepository.findByRequestId("req_1")).thenReturn(Optional.of(queueEntry1));

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> 
            randomVideoCallService.acceptMatchedCall("req_1"));
    }

    @Test
    void declineMatchedCall_ShouldDeclineCall() {
        // Arrange
        queueEntry1.setQueueStatus("matched");
        when(userRepository.findById(3L)).thenReturn(Optional.of(user1));
        when(queueRepository.findByRequestId("req_1")).thenReturn(Optional.of(queueEntry1));

        // Act
        randomVideoCallService.declineMatchedCall("req_1", "Not interested");

        // Assert
        assertEquals("declined", queueEntry1.getQueueStatus());
        assertEquals("Not interested", queueEntry1.getErrorMessage());
        verify(queueRepository).save(queueEntry1);
    }

    @Test
    void getQueueStatistics_ShouldReturnStatistics() {
        // Arrange
        when(queueRepository.countWaitingUsers()).thenReturn(2L);

        // Act
        RandomVideoCallService.QueueStatistics statistics = randomVideoCallService.getQueueStatistics();

        // Assert
        assertNotNull(statistics);
        assertEquals(2L, statistics.getTotalUsersInQueue());
    }

    @Test
    void getMatchingSystemStatus_ShouldReturnStatus() {
        // Arrange
        ReflectionTestUtils.setField(randomVideoCallService, "matchingEnabled", true);
        ReflectionTestUtils.setField(randomVideoCallService, "lastMatchTime", 1234567890L);
        ReflectionTestUtils.setField(randomVideoCallService, "matchesToday", 5L);
        when(queueRepository.countWaitingUsers()).thenReturn(2L);

        // Act
        RandomVideoCallService.MatchingSystemStatus status = randomVideoCallService.getMatchingSystemStatus();

        // Assert
        assertNotNull(status);
        assertTrue(status.isMatchingEnabled());
        assertEquals(2L, status.getTotalUsersInQueue());
        assertEquals(1234567890L, status.getLastMatchTime());
        assertEquals(5L, status.getMatchesToday());
        assertEquals("ACTIVE", status.getStatus());
    }

    @Test
    void enableMatching_ShouldEnableMatching() {
        // Act
        randomVideoCallService.enableMatching();

        // Assert
        assertTrue(randomVideoCallService.isMatchingEnabled());
    }

    @Test
    void disableMatching_ShouldDisableMatching() {
        // Act
        randomVideoCallService.disableMatching();

        // Assert
        assertFalse(randomVideoCallService.isMatchingEnabled());
    }

    @Test
    void getCurrentUserActiveRequest_ShouldReturnActiveRequest() {
        // Arrange
        when(userRepository.findById(3L)).thenReturn(Optional.of(user1));
        when(queueRepository.findActiveRequestByUser(user1)).thenReturn(Optional.of(queueEntry1));

        // Act
        RandomVideoCallResponseDto response = randomVideoCallService.getCurrentUserActiveRequest();

        // Assert
        assertNotNull(response);
        assertEquals("req_1", response.getRequestId());
        assertEquals("waiting", response.getQueueStatus());
    }

    @Test
    void getCurrentUserActiveRequest_ShouldReturnNull_WhenNoActiveRequest() {
        // Arrange
        when(userRepository.findById(3L)).thenReturn(Optional.of(user1));
        when(queueRepository.findActiveRequestByUser(user1)).thenReturn(Optional.empty());

        // Act
        RandomVideoCallResponseDto response = randomVideoCallService.getCurrentUserActiveRequest();

        // Assert
        assertNull(response);
    }
} 