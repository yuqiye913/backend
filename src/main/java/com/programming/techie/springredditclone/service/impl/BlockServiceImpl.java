package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.BlockRequestDto;
import com.programming.techie.springredditclone.dto.BlockResponseDto;
import com.programming.techie.springredditclone.dto.BlockListResponseDto;
import com.programming.techie.springredditclone.dto.UnblockRequestDto;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.Block;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.BlockRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.BlockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    public BlockResponseDto blockUser(BlockRequestDto blockRequest) {
        User currentUser = authService.getCurrentUser();
        User userToBlock = userRepository.findById(blockRequest.getBlockedUserId())
                .orElseThrow(() -> new SpringRedditException("User not found with ID: " + blockRequest.getBlockedUserId()));

        // Check if user is trying to block themselves
        if (currentUser.getUserId().equals(blockRequest.getBlockedUserId())) {
            throw new SpringRedditException("Cannot block yourself");
        }

        // Check if already blocked
        Optional<Block> existingBlock = blockRepository.findByBlockerAndBlocked(currentUser, userToBlock);
        if (existingBlock.isPresent()) {
            Block block = existingBlock.get();
            if (block.isActive()) {
                throw new SpringRedditException("User is already blocked");
            } else {
                // Reactivate existing block
                block.setActive(true);
                block.setBlockedAt(Instant.now());
                block.setReason(blockRequest.getReason());
                block = blockRepository.save(block);
                return mapToBlockResponseDto(block, currentUser, userToBlock);
            }
        }

        // Create new block
        Block block = new Block();
        block.setBlocker(currentUser);
        block.setBlocked(userToBlock);
        block.setReason(blockRequest.getReason());
        block.setActive(true);
        block.setBlockedAt(Instant.now());



        block = blockRepository.save(block);
        log.info("User {} blocked user {}", currentUser.getUsername(), userToBlock.getUsername());

        return mapToBlockResponseDto(block, currentUser, userToBlock);
    }

    @Override
    public BlockResponseDto unblockUser(UnblockRequestDto unblockRequest) {
        User currentUser = authService.getCurrentUser();
        User userToUnblock = userRepository.findById(unblockRequest.getBlockedUserId())
                .orElseThrow(() -> new SpringRedditException("User not found with ID: " + unblockRequest.getBlockedUserId()));

        Block block = blockRepository.findByBlockerAndBlocked(currentUser, userToUnblock)
                .orElseThrow(() -> new SpringRedditException("No active block found for this user"));

        if (!block.isActive()) {
            throw new SpringRedditException("User is not currently blocked");
        }

        // Soft delete by setting active to false
        block.setActive(false);
        block = blockRepository.save(block);
        log.info("User {} unblocked user {}", currentUser.getUsername(), userToUnblock.getUsername());

        return mapToBlockResponseDto(block, currentUser, userToUnblock);
    }

    @Override
    @Transactional(readOnly = true)
    public BlockListResponseDto getBlockedUsers(int page, int size) {
        User currentUser = authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        
        // Only get active blocks from the database
        Page<Block> blockPage = blockRepository.findByBlockerAndIsActive(currentUser, true, pageable);
        List<BlockResponseDto> blockedUsers = blockPage.getContent().stream()
                .map(block -> mapToBlockResponseDto(block, currentUser, block.getBlocked()))
                .collect(Collectors.toList());

        return createBlockListResponse(blockedUsers, blockPage, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public BlockListResponseDto getUsersWhoBlockedMe(int page, int size) {
        User currentUser = authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Block> blockPage = blockRepository.findByBlocked(currentUser, pageable);
        List<BlockResponseDto> blockingUsers = blockPage.getContent().stream()
                .map(block -> mapToBlockResponseDto(block, block.getBlocker(), currentUser))
                .collect(Collectors.toList());

        return createBlockListResponse(blockingUsers, blockPage, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public BlockResponseDto getBlockById(Long blockId) {
        User currentUser = authService.getCurrentUser();
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new SpringRedditException("Block not found with ID: " + blockId));

        // Ensure the current user is involved in this block
        if (!block.getBlocker().getUserId().equals(currentUser.getUserId()) && 
            !block.getBlocked().getUserId().equals(currentUser.getUserId())) {
            throw new SpringRedditException("Access denied to this block");
        }

        return mapToBlockResponseDto(block, block.getBlocker(), block.getBlocked());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasBlockedUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || authentication instanceof AnonymousAuthenticationToken
            || !authentication.isAuthenticated()
            || !(authentication.getPrincipal() instanceof Jwt)) {
            return false; // Not logged in, so can't have blocked anyone
        }
        User currentUser = authService.getCurrentUser();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new SpringRedditException("User not found with ID: " + userId));

        // Check if there's an active block
        return blockRepository.existsByBlockerAndBlockedAndIsActive(currentUser, targetUser, true);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlockedByUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || authentication instanceof AnonymousAuthenticationToken
            || !authentication.isAuthenticated()
            || !(authentication.getPrincipal() instanceof Jwt)) {
            return false; // Not logged in, so can't be blocked
        }
        User currentUser = authService.getCurrentUser();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new SpringRedditException("User not found with ID: " + userId));

        return blockRepository.existsByBlockerAndBlocked(targetUser, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMutualBlock(Long userId) {
        return hasBlockedUser(userId) && isBlockedByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BlockStatistics getBlockStatistics() {
        User currentUser = authService.getCurrentUser();
        
        long totalBlocksByUser = blockRepository.findByBlocker(currentUser).size();
        long totalBlocksOfUser = blockRepository.findByBlocked(currentUser).size();
        long activeBlockCount = blockRepository.findByBlocker(currentUser).stream()
                .filter(Block::isActive).count();
        long mutualBlockCount = 0; // Would need to implement mutual block detection
        
        // Get most common block reason
        String mostCommonBlockReason = blockRepository.findByBlocker(currentUser).stream()
                .map(Block::getReason)
                .filter(reason -> reason != null && !reason.isEmpty())
                .collect(Collectors.groupingBy(reason -> reason, Collectors.counting()))
                .entrySet().stream()
                .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .map(entry -> entry.getKey())
                .orElse("No reason provided");

        return new BlockStatistics(totalBlocksByUser, totalBlocksOfUser, activeBlockCount,
                mutualBlockCount, mostCommonBlockReason);
    }

    @Override
    public BlockResponseDto updateBlock(Long blockId, BlockRequestDto blockRequest) {
        User currentUser = authService.getCurrentUser();
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new SpringRedditException("Block not found with ID: " + blockId));

        // Ensure the current user is the blocker
        if (!block.getBlocker().getUserId().equals(currentUser.getUserId())) {
            throw new SpringRedditException("Access denied to update this block");
        }

        // Update block properties
        if (blockRequest.getReason() != null) {
            block.setReason(blockRequest.getReason());
        }

        block = blockRepository.save(block);
        return mapToBlockResponseDto(block, currentUser, block.getBlocked());
    }

    @Override
    public void deleteBlock(Long blockId) {
        User currentUser = authService.getCurrentUser();
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new SpringRedditException("Block not found with ID: " + blockId));

        // Ensure the current user is the blocker
        if (!block.getBlocker().getUserId().equals(currentUser.getUserId())) {
            throw new SpringRedditException("Access denied to delete this block");
        }

        blockRepository.delete(block);
        log.info("Block {} deleted by user {}", blockId, currentUser.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockResponseDto> getActiveBlocks() {
        User currentUser = authService.getCurrentUser();
        return blockRepository.findByBlockerAndIsActive(currentUser, true).stream()
                .map(block -> mapToBlockResponseDto(block, currentUser, block.getBlocked()))
                .collect(Collectors.toList());
    }



    private BlockResponseDto mapToBlockResponseDto(Block block, User blocker, User blocked) {
        BlockResponseDto dto = new BlockResponseDto();
        dto.setBlockId(block.getId());
        dto.setBlockerId(blocker.getUserId());
        dto.setBlockedUserId(blocked.getUserId());
        dto.setBlockerUsername(blocker.getUsername());
        dto.setBlockedUserUsername(blocked.getUsername());
        dto.setReason(block.getReason());
        dto.setBlockedAt(block.getBlockedAt() != null ? block.getBlockedAt().toEpochMilli() : null);
        dto.setActive(block.isActive());
        

        
        // Calculate statistics
        long totalBlocksByUser = blockRepository.findByBlocker(blocker).size();
        long totalBlocksOfUser = blockRepository.findByBlocked(blocked).size();
        dto.setTotalBlocksByUser(totalBlocksByUser);
        dto.setTotalBlocksOfUser(totalBlocksOfUser);
        dto.setMutualBlock(isMutualBlock(blocked.getUserId()));
        
        return dto;
    }

    private BlockListResponseDto createBlockListResponse(List<BlockResponseDto> blocks, Page<Block> blockPage, User currentUser) {
        BlockListResponseDto response = new BlockListResponseDto();
        response.setBlockedUsers(blocks);
        response.setTotalCount(blockPage.getTotalElements());
        response.setActiveBlockCount(blockPage.getContent().stream().filter(Block::isActive).count());
        response.setHasMore(blockPage.hasNext());
        response.setNextPage(blockPage.hasNext() ? (long) (blockPage.getNumber() + 1) : null);
        response.setCurrentPage((long) blockPage.getNumber());
        response.setPageSize((long) blockPage.getSize());
        
        // Calculate statistics
        long totalBlocksByUser = blockRepository.findByBlocker(currentUser).size();
        long totalBlocksOfUser = blockRepository.findByBlocked(currentUser).size();
        response.setTotalBlocksByUser(totalBlocksByUser);
        response.setTotalBlocksOfUser(totalBlocksOfUser);
        
        return response;
    }
} 