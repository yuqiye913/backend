package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.BlockListResponseDto;
import com.programming.techie.springredditclone.dto.BlockRequestDto;
import com.programming.techie.springredditclone.dto.BlockResponseDto;
import com.programming.techie.springredditclone.dto.UnblockRequestDto;
import com.programming.techie.springredditclone.model.Block;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.BlockRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.impl.BlockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlockServiceTest {

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private BlockServiceImpl blockService;

    private User currentUser;
    private User userToBlock;
    private Block block;

    @BeforeEach
    void setUp() {
        // Create current user
        currentUser = new User();
        currentUser.setUserId(1L);
        currentUser.setUsername("currentuser");
        currentUser.setEmail("current@example.com");
        currentUser.setEnabled(true);

        // Create user to block
        userToBlock = new User();
        userToBlock.setUserId(2L);
        userToBlock.setUsername("usertoblock");
        userToBlock.setEmail("block@example.com");
        userToBlock.setEnabled(true);

        // Create block
        block = new Block();
        block.setId(1L);
        block.setBlocker(currentUser);
        block.setBlocked(userToBlock);
        block.setBlockedAt(Instant.now());
        block.setReason("Test reason");
        block.setActive(true);
    }

    @Test
    @DisplayName("Should block user successfully")
    void shouldBlockUserSuccessfully() {
        // Given
        BlockRequestDto request = new BlockRequestDto();
        request.setBlockedUserId(2L);
        request.setReason("Test reason");

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.existsByBlockerAndBlocked(currentUser, userToBlock)).thenReturn(false);
        when(blockRepository.save(any(Block.class))).thenReturn(block);
        when(blockRepository.findByBlocker(any(User.class))).thenReturn(Arrays.asList(block));
        when(blockRepository.findByBlocked(any(User.class))).thenReturn(Arrays.asList());
        when(blockRepository.existsByBlockerAndBlocked(any(User.class), any(User.class))).thenReturn(false);

        // When
        BlockResponseDto response = blockService.blockUser(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBlockId()).isEqualTo(1L);
        assertThat(response.getBlockerId()).isEqualTo(1L);
        assertThat(response.getBlockedUserId()).isEqualTo(2L);
        assertThat(response.getReason()).isEqualTo("Test reason");
        assertThat(response.isActive()).isTrue();

        verify(blockRepository).save(any(Block.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to block yourself")
    void shouldThrowExceptionWhenTryingToBlockYourself() {
        // Given
        BlockRequestDto request = new BlockRequestDto();
        request.setBlockedUserId(1L); // Same as current user

        when(authService.getCurrentUser()).thenReturn(currentUser);

        // When & Then
        assertThatThrownBy(() -> blockService.blockUser(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should throw exception when user to block not found")
    void shouldThrowExceptionWhenUserToBlockNotFound() {
        // Given
        BlockRequestDto request = new BlockRequestDto();
        request.setBlockedUserId(999L);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> blockService.blockUser(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should throw exception when user is already blocked")
    void shouldThrowExceptionWhenUserAlreadyBlocked() {
        // Given
        BlockRequestDto request = new BlockRequestDto();
        request.setBlockedUserId(2L);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.existsByBlockerAndBlocked(currentUser, userToBlock)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> blockService.blockUser(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should unblock user successfully")
    void shouldUnblockUserSuccessfully() {
        // Given
        UnblockRequestDto request = new UnblockRequestDto();
        request.setBlockedUserId(2L);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.findByBlockerAndBlocked(currentUser, userToBlock)).thenReturn(Optional.of(block));
        when(blockRepository.save(any(Block.class))).thenReturn(block);
        when(blockRepository.findByBlocker(any(User.class))).thenReturn(Arrays.asList(block));
        when(blockRepository.findByBlocked(any(User.class))).thenReturn(Arrays.asList());
        when(blockRepository.existsByBlockerAndBlocked(any(User.class), any(User.class))).thenReturn(false);

        // When
        BlockResponseDto response = blockService.unblockUser(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBlockId()).isEqualTo(1L);
        assertThat(response.getBlockerId()).isEqualTo(1L);
        assertThat(response.getBlockedUserId()).isEqualTo(2L);
        assertThat(response.isActive()).isFalse();

        verify(blockRepository).save(any(Block.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to unblock yourself")
    void shouldThrowExceptionWhenTryingToUnblockYourself() {
        // Given
        UnblockRequestDto request = new UnblockRequestDto();
        request.setBlockedUserId(1L); // Same as current user

        when(authService.getCurrentUser()).thenReturn(currentUser);

        // When & Then
        assertThatThrownBy(() -> blockService.unblockUser(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should throw exception when user to unblock not found")
    void shouldThrowExceptionWhenUserToUnblockNotFound() {
        // Given
        UnblockRequestDto request = new UnblockRequestDto();
        request.setBlockedUserId(999L);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> blockService.unblockUser(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should throw exception when user is not blocked")
    void shouldThrowExceptionWhenUserNotBlocked() {
        // Given
        UnblockRequestDto request = new UnblockRequestDto();
        request.setBlockedUserId(2L);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.findByBlockerAndBlocked(currentUser, userToBlock)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> blockService.unblockUser(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should get blocked users with pagination")
    void shouldGetBlockedUsersWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Block> blocks = Arrays.asList(block);
        Page<Block> blockPage = new PageImpl<>(blocks, pageable, 1);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(blockRepository.findByBlocker(currentUser, pageable)).thenReturn(blockPage);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.findByBlocker(any(User.class))).thenReturn(blocks);
        when(blockRepository.findByBlocked(any(User.class))).thenReturn(Arrays.asList());

        // When
        BlockListResponseDto response = blockService.getBlockedUsers(0, 10);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBlockedUsers()).hasSize(1);
        assertThat(response.getTotalCount()).isEqualTo(1L);
        assertThat(response.getCurrentPage()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should get users who blocked me with pagination")
    void shouldGetUsersWhoBlockedMeWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Block> blocks = Arrays.asList(block);
        Page<Block> blockPage = new PageImpl<>(blocks, pageable, 1);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(blockRepository.findByBlocked(currentUser, pageable)).thenReturn(blockPage);
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(blockRepository.findByBlocker(any(User.class))).thenReturn(Arrays.asList());
        when(blockRepository.findByBlocked(any(User.class))).thenReturn(blocks);

        // When
        BlockListResponseDto response = blockService.getUsersWhoBlockedMe(0, 10);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBlockedUsers()).hasSize(1);
        assertThat(response.getTotalCount()).isEqualTo(1L);
        assertThat(response.getCurrentPage()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should get block by ID")
    void shouldGetBlockById() {
        // Given
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(blockRepository.findById(1L)).thenReturn(Optional.of(block));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.findByBlocker(any(User.class))).thenReturn(Arrays.asList(block));
        when(blockRepository.findByBlocked(any(User.class))).thenReturn(Arrays.asList());
        when(blockRepository.existsByBlockerAndBlocked(any(User.class), any(User.class))).thenReturn(false);

        // When
        BlockResponseDto response = blockService.getBlockById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBlockId()).isEqualTo(1L);
        assertThat(response.getBlockerId()).isEqualTo(1L);
        assertThat(response.getBlockedUserId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should throw exception when block not found")
    void shouldThrowExceptionWhenBlockNotFound() {
        // Given
        when(blockRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> blockService.getBlockById(999L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should return true when user has blocked another user")
    void shouldReturnTrueWhenUserHasBlockedAnotherUser() {
        // Given
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.existsByBlockerAndBlocked(currentUser, userToBlock)).thenReturn(true);

        // When
        boolean result = blockService.hasBlockedUser(2L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when user has not blocked another user")
    void shouldReturnFalseWhenUserHasNotBlockedAnotherUser() {
        // Given
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.existsByBlockerAndBlocked(currentUser, userToBlock)).thenReturn(false);

        // When
        boolean result = blockService.hasBlockedUser(2L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true when user is blocked by another user")
    void shouldReturnTrueWhenUserIsBlockedByAnotherUser() {
        // Given
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.existsByBlockerAndBlocked(userToBlock, currentUser)).thenReturn(true);

        // When
        boolean result = blockService.isBlockedByUser(2L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when user is not blocked by another user")
    void shouldReturnFalseWhenUserIsNotBlockedByAnotherUser() {
        // Given
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));
        when(blockRepository.existsByBlockerAndBlocked(userToBlock, currentUser)).thenReturn(false);

        // When
        boolean result = blockService.isBlockedByUser(2L);

        // Then
        assertThat(result).isFalse();
    }
} 