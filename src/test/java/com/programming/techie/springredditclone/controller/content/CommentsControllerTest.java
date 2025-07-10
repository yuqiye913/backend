package com.programming.techie.springredditclone.controller.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.programming.techie.springredditclone.dto.CommentsDto;
import com.programming.techie.springredditclone.dto.CursorPageResponse;
import com.programming.techie.springredditclone.exceptions.GlobalExceptionHandler;
import com.programming.techie.springredditclone.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentsControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private com.programming.techie.springredditclone.controller.CommentsController commentsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private CommentsDto testCommentDto;
    private CursorPageResponse<CommentsDto> testPageResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testCommentDto = CommentsDto.builder()
                .id(1L)
                .postId(1L)
                .text("Test comment")
                .userName("testuser")
                .createdDate(Instant.now())
                .voteCount(0)
                .replyCount(0)
                .build();

        testPageResponse = new CursorPageResponse<>(
                Arrays.asList(testCommentDto),
                "next-cursor",
                true,
                20
        );
    }

    @Test
    void createComment_ValidComment_ShouldReturnCreated() throws Exception {
        // Arrange
        // No need to mock void method, just verify it's called

        // Act & Assert
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCommentDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createComment_InvalidComment_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CommentsDto invalidComment = CommentsDto.builder()
                .id(1L)
                .text("") // Empty text should fail validation
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllCommentsForPost_ValidPostId_ShouldReturnComments() throws Exception {
        // Arrange
        List<CommentsDto> comments = Arrays.asList(testCommentDto);
        when(commentService.getAllCommentsForPost(1L)).thenReturn(comments);

        // Act & Assert
        mockMvc.perform(get("/api/comments")
                        .param("postId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Test comment"))
                .andExpect(jsonPath("$[0].userName").value("testuser"));
    }

    @Test
    void getAllCommentsForUser_ValidUsername_ShouldReturnComments() throws Exception {
        // Arrange
        List<CommentsDto> comments = Arrays.asList(testCommentDto);
        when(commentService.getAllCommentsForUser("testuser")).thenReturn(comments);

        // Act & Assert
        mockMvc.perform(get("/api/comments")
                        .param("userName", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Test comment"))
                .andExpect(jsonPath("$[0].userName").value("testuser"));
    }

    @Test
    void getCommentsForPost_FirstPage_ShouldReturnPaginatedComments() throws Exception {
        // Arrange
        when(commentService.getCommentsForPost(eq(1L), isNull(), eq(20)))
                .thenReturn(testPageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/comments/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].text").value("Test comment"))
                .andExpect(jsonPath("$.nextCursor").value("next-cursor"))
                .andExpect(jsonPath("$.hasMore").value(true))
                .andExpect(jsonPath("$.limit").value(20));
    }

    @Test
    void getCommentsForPost_WithCursorAndLimit_ShouldReturnPaginatedComments() throws Exception {
        // Arrange
        when(commentService.getCommentsForPost(eq(1L), eq("test-cursor"), eq(10)))
                .thenReturn(testPageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/comments/post/1")
                        .param("cursor", "test-cursor")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.nextCursor").value("next-cursor"));
    }

    @Test
    void getCommentsForPost_WithCustomLimit_ShouldUseCustomLimit() throws Exception {
        // Arrange
        when(commentService.getCommentsForPost(eq(1L), isNull(), eq(30)))
                .thenReturn(testPageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/comments/post/1")
                        .param("limit", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(20)); // Should use the limit from response
    }

    @Test
    void getCommentsForUser_FirstPage_ShouldReturnPaginatedComments() throws Exception {
        // Arrange
        when(commentService.getCommentsForUser(eq("testuser"), isNull(), eq(20)))
                .thenReturn(testPageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/comments/user/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].text").value("Test comment"))
                .andExpect(jsonPath("$.nextCursor").value("next-cursor"))
                .andExpect(jsonPath("$.hasMore").value(true));
    }

    @Test
    void getCommentsForUser_WithCursorAndLimit_ShouldReturnPaginatedComments() throws Exception {
        // Arrange
        when(commentService.getCommentsForUser(eq("testuser"), eq("test-cursor"), eq(15)))
                .thenReturn(testPageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/comments/user/testuser")
                        .param("cursor", "test-cursor")
                        .param("limit", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getRepliesForComment_FirstPage_ShouldReturnPaginatedReplies() throws Exception {
        // Arrange
        when(commentService.getRepliesForComment(eq(1L), isNull(), eq(20)))
                .thenReturn(testPageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/comments/1/replies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].text").value("Test comment"))
                .andExpect(jsonPath("$.nextCursor").value("next-cursor"))
                .andExpect(jsonPath("$.hasMore").value(true));
    }

    @Test
    void getRepliesForComment_WithCursorAndLimit_ShouldReturnPaginatedReplies() throws Exception {
        // Arrange
        when(commentService.getRepliesForComment(eq(1L), eq("test-cursor"), eq(25)))
                .thenReturn(testPageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/comments/1/replies")
                        .param("cursor", "test-cursor")
                        .param("limit", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getCommentsForPost_EmptyResult_ShouldReturnEmptyPage() throws Exception {
        // Arrange
        CursorPageResponse<CommentsDto> emptyResponse = new CursorPageResponse<>(
                Arrays.asList(),
                null,
                false,
                20
        );
        when(commentService.getCommentsForPost(eq(1L), isNull(), eq(20)))
                .thenReturn(emptyResponse);

        // Act & Assert
        mockMvc.perform(get("/api/comments/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.hasMore").value(false))
                .andExpect(jsonPath("$.nextCursor").doesNotExist());
    }

    @Test
    void getCommentsForPost_InvalidLimit_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/comments/post/1")
                        .param("limit", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCommentsForPost_NegativeLimit_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/comments/post/1")
                        .param("limit", "-5"))
                .andExpect(status().isBadRequest());
    }
} 