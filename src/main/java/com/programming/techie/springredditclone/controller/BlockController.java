package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.BlockRequestDto;
import com.programming.techie.springredditclone.dto.BlockResponseDto;
import com.programming.techie.springredditclone.dto.BlockListResponseDto;
import com.programming.techie.springredditclone.dto.UnblockRequestDto;
import com.programming.techie.springredditclone.service.BlockService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blocks")
@AllArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<BlockResponseDto> blockUser(@RequestBody BlockRequestDto blockRequest) {
        BlockResponseDto response = blockService.blockUser(blockRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<BlockResponseDto> unblockUser(@RequestBody UnblockRequestDto unblockRequest) {
        BlockResponseDto response = blockService.unblockUser(unblockRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unblock")
    public ResponseEntity<BlockResponseDto> unblockUserPost(@RequestBody UnblockRequestDto unblockRequest) {
        BlockResponseDto response = blockService.unblockUser(unblockRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<BlockListResponseDto> getBlockedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        BlockListResponseDto response = blockService.getBlockedUsers(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/blocked-by")
    public ResponseEntity<BlockListResponseDto> getUsersWhoBlockedMe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        BlockListResponseDto response = blockService.getUsersWhoBlockedMe(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{blockId}")
    public ResponseEntity<BlockResponseDto> getBlockById(@PathVariable Long blockId) {
        BlockResponseDto response = blockService.getBlockById(blockId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/has-blocked/{userId}")
    public ResponseEntity<Boolean> hasBlockedUser(@PathVariable Long userId) {
        boolean hasBlocked = blockService.hasBlockedUser(userId);
        return ResponseEntity.ok(hasBlocked);
    }

    @GetMapping("/is-blocked-by/{userId}")
    public ResponseEntity<Boolean> isBlockedByUser(@PathVariable Long userId) {
        boolean isBlocked = blockService.isBlockedByUser(userId);
        return ResponseEntity.ok(isBlocked);
    }
} 