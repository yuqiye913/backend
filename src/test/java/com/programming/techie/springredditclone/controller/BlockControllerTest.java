package com.programming.techie.springredditclone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.dto.BlockRequestDto;
import com.programming.techie.springredditclone.dto.BlockResponseDto;
import com.programming.techie.springredditclone.dto.BlockListResponseDto;
import com.programming.techie.springredditclone.dto.UnblockRequestDto;
import com.programming.techie.springredditclone.service.BlockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BlockControllerTest {

    @Mock
    private BlockService blockService;

    @InjectMocks
    private BlockController blockController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(blockController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should block user successfully")
    void shouldBlockUserSuccessfully() throws Exception {
        // Given
        BlockRequestDto request = new BlockRequestDto();
        request.setBlockedUserId(2L);
        request.setReason("Test reason");

        BlockResponseDto response = new BlockResponseDto();
        response.setBlockId(1L);
        response.setBlockerId(1L);
        response.setBlockedUserId(2L);
        response.setReason("Test reason");
        response.setActive(true);
        response.setBlockedAt(Instant.now().toEpochMilli());

        when(blockService.blockUser(any(BlockRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blockId").value(1))
                .andExpect(jsonPath("$.blockerId").value(1))
                .andExpect(jsonPath("$.blockedUserId").value(2))
                .andExpect(jsonPath("$.reason").value("Test reason"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should unblock user successfully")
    void shouldUnblockUserSuccessfully() throws Exception {
        // Given
        UnblockRequestDto request = new UnblockRequestDto();
        request.setBlockedUserId(2L);

        BlockResponseDto response = new BlockResponseDto();
        response.setBlockId(1L);
        response.setBlockerId(1L);
        response.setBlockedUserId(2L);
        response.setActive(false);
        response.setBlockedAt(Instant.now().toEpochMilli());

        when(blockService.unblockUser(any(UnblockRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(delete("/api/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blockId").value(1))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("Should get blocked users")
    void shouldGetBlockedUsers() throws Exception {
        // Given
        BlockListResponseDto response = new BlockListResponseDto();
        BlockResponseDto dto = new BlockResponseDto();
        dto.setBlockId(1L);
        dto.setBlockerId(1L);
        dto.setBlockedUserId(2L);
        dto.setReason("Test reason");
        dto.setActive(true);
        dto.setBlockedAt(Instant.now().toEpochMilli());
        response.setBlockedUsers(Arrays.asList(dto));
        response.setTotalCount(1L);
        response.setCurrentPage(0L);

        when(blockService.getBlockedUsers(eq(0), eq(10))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/blocks")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blockedUsers").isArray())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    @DisplayName("Should get users who blocked me")
    void shouldGetUsersWhoBlockedMe() throws Exception {
        // Given
        BlockListResponseDto response = new BlockListResponseDto();
        BlockResponseDto dto2 = new BlockResponseDto();
        dto2.setBlockId(1L);
        dto2.setBlockerId(2L);
        dto2.setBlockedUserId(1L);
        dto2.setReason("Test reason");
        dto2.setActive(true);
        dto2.setBlockedAt(Instant.now().toEpochMilli());
        response.setBlockedUsers(Arrays.asList(dto2));
        response.setTotalCount(1L);
        response.setCurrentPage(0L);

        when(blockService.getUsersWhoBlockedMe(eq(0), eq(10))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/blocks/blocked-by")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blockedUsers").isArray())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    @DisplayName("Should get block by ID")
    void shouldGetBlockById() throws Exception {
        // Given
        BlockResponseDto response = new BlockResponseDto();
        response.setBlockId(1L);
        response.setBlockerId(1L);
        response.setBlockedUserId(2L);
        response.setReason("Test reason");
        response.setActive(true);
        response.setBlockedAt(Instant.now().toEpochMilli());

        when(blockService.getBlockById(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/blocks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blockId").value(1))
                .andExpect(jsonPath("$.blockerId").value(1))
                .andExpect(jsonPath("$.blockedUserId").value(2))
                .andExpect(jsonPath("$.reason").value("Test reason"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should check if user has blocked another user")
    void shouldCheckIfUserHasBlockedAnotherUser() throws Exception {
        // Given
        when(blockService.hasBlockedUser(2L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/blocks/has-blocked/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("Should check if user is blocked by another user")
    void shouldCheckIfUserIsBlockedByAnotherUser() throws Exception {
        // Given
        when(blockService.isBlockedByUser(2L)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/blocks/is-blocked-by/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
} 