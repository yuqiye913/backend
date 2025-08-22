package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.GetIntroDto;
import com.programming.techie.springredditclone.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.programming.techie.springredditclone.config.TestSecurityConfig;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private GetIntroDto sampleUserIntro;

    @BeforeEach
    void setUp() {
        // Create sample user intro
        sampleUserIntro = new GetIntroDto(
            1L,
            "testuser",
            "This is my bio"
        );
    }

    @Test
    @DisplayName("Should get user intro by user ID")
    @WithMockUser(username = "testuser")
    void shouldGetUserIntroByUserId() throws Exception {
        // Given
        when(userService.getUserIntro(eq(1L))).thenReturn(sampleUserIntro);

        // When & Then
        mockMvc.perform(get("/api/user/intro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.bio").value("This is my bio"));

        verify(userService).getUserIntro(1L);
    }

    @Test
    @DisplayName("Should return basic user info when no intro exists")
    @WithMockUser(username = "testuser")
    void shouldReturnBasicUserInfoWhenNoIntroExists() throws Exception {
        // Given
        GetIntroDto basicUserInfo = new GetIntroDto(1L, "testuser", null);
        when(userService.getUserIntro(eq(1L))).thenReturn(basicUserInfo);

        // When & Then
        mockMvc.perform(get("/api/user/intro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.bio").isEmpty());

        verify(userService).getUserIntro(1L);
    }

    @Test
    @DisplayName("Should handle service exception when getting user intro")
    @WithMockUser(username = "testuser")
    void shouldHandleServiceExceptionWhenGettingUserIntro() throws Exception {
        // Given
        doThrow(new RuntimeException("User not found"))
                .when(userService).getUserIntro(eq(999L));

        // When & Then
        mockMvc.perform(get("/api/user/intro/999"))
                .andExpect(status().isInternalServerError());

        verify(userService).getUserIntro(999L);
    }

    @Test
    @DisplayName("Should require authentication for get user intro endpoint")
    void shouldRequireAuthenticationForGetUserIntroEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/intro/1"))
                .andExpect(status().isForbidden());
    }
} 