package com.programming.techie.springredditclone.controller.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.config.TestSecurityConfig;
import com.programming.techie.springredditclone.controller.PostController;
import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.dto.PostResponse;
import com.programming.techie.springredditclone.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private PostRequest validPostRequest;
    private PostResponse samplePostResponse;

    @BeforeEach
    void setUp() {
        // Create valid post request with multiple subreddits
        validPostRequest = new PostRequest();
        validPostRequest.setPostName("Test Post");
        validPostRequest.setDescription("Test Description");
        validPostRequest.setUrl("http://example.com");
        validPostRequest.setSubredditNames(List.of("programming", "gaming"));

        // Create sample post response
        samplePostResponse = new PostResponse();
        samplePostResponse.setId(1L);
        samplePostResponse.setPostName("Test Post");
        samplePostResponse.setSubredditNames(List.of("programming", "gaming"));
    }

    @Test
    @DisplayName("Should successfully create post with multiple subreddits")
    void shouldCreatePostWithMultipleSubreddits() throws Exception {
        // Given
        doNothing().when(postService).save(any(PostRequest.class));

        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isCreated());

        verify(postService).save(any(PostRequest.class));
    }

    @Test
    @DisplayName("Should successfully create post with single subreddit")
    void shouldCreatePostWithSingleSubreddit() throws Exception {
        // Given
        validPostRequest.setSubredditNames(List.of("programming"));
        doNothing().when(postService).save(any(PostRequest.class));

        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isCreated());

        verify(postService).save(any(PostRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when post name is missing")
    void shouldReturnBadRequestWhenPostNameMissing() throws Exception {
        // Given
        validPostRequest.setPostName(null);

        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when subreddit names are missing")
    void shouldReturnBadRequestWhenSubredditNamesMissing() throws Exception {
        // Given
        validPostRequest.setSubredditNames(null);

        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when subreddit names list is empty")
    void shouldReturnBadRequestWhenSubredditNamesEmpty() throws Exception {
        // Given
        validPostRequest.setSubredditNames(List.of());

        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid JSON")
    void shouldReturnBadRequestForInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    void shouldHandleServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Service error")).when(postService).save(any(PostRequest.class));

        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isInternalServerError());

        verify(postService).save(any(PostRequest.class));
    }

    @Test
    @DisplayName("Should get all posts successfully")
    void shouldGetAllPosts() throws Exception {
        // Given
        List<PostResponse> posts = List.of(samplePostResponse);
        when(postService.getAllPosts()).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk());

        verify(postService).getAllPosts();
    }

    @Test
    @DisplayName("Should get post by ID successfully")
    void shouldGetPostById() throws Exception {
        // Given
        when(postService.getPost(1L)).thenReturn(samplePostResponse);

        // When & Then
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk());

        verify(postService).getPost(1L);
    }

    @Test
    @DisplayName("Should get posts by subreddit ID successfully")
    void shouldGetPostsBySubredditId() throws Exception {
        // Given
        List<PostResponse> posts = List.of(samplePostResponse);
        when(postService.getPostsBySubreddit(1L)).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/posts?subredditId=1"))
                .andExpect(status().isOk());

        verify(postService).getPostsBySubreddit(1L);
    }

    @Test
    @DisplayName("Should get posts by username successfully")
    void shouldGetPostsByUsername() throws Exception {
        // Given
        List<PostResponse> posts = List.of(samplePostResponse);
        when(postService.getPostsByUsername("testuser")).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/posts?username=testuser"))
                .andExpect(status().isOk());

        verify(postService).getPostsByUsername("testuser");
    }

    @Test
    @DisplayName("Should get posts by multiple subreddits successfully")
    void shouldGetPostsByMultipleSubreddits() throws Exception {
        // Given
        List<String> subredditNames = List.of("programming", "gaming");
        List<PostResponse> posts = List.of(samplePostResponse);
        when(postService.getPostsByMultipleSubreddits(subredditNames)).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/posts/by-subreddits")
                .param("subredditNames", "programming", "gaming"))
                .andExpect(status().isOk());

        verify(postService).getPostsByMultipleSubreddits(subredditNames);
    }

    @Test
    @DisplayName("Should handle service exception for get post by ID")
    void shouldHandleServiceExceptionForGetPostById() throws Exception {
        // Given
        when(postService.getPost(999L)).thenThrow(new RuntimeException("Post not found"));

        // When & Then
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isInternalServerError());

        verify(postService).getPost(999L);
    }

    @Test
    @DisplayName("Should handle service exception for get posts by subreddit")
    void shouldHandleServiceExceptionForGetPostsBySubreddit() throws Exception {
        // Given
        when(postService.getPostsBySubreddit(999L)).thenThrow(new RuntimeException("Subreddit not found"));

        // When & Then
        mockMvc.perform(get("/api/posts?subredditId=999"))
                .andExpect(status().isInternalServerError());

        verify(postService).getPostsBySubreddit(999L);
    }

    @Test
    @DisplayName("Should handle service exception for get posts by username")
    void shouldHandleServiceExceptionForGetPostsByUsername() throws Exception {
        // Given
        when(postService.getPostsByUsername("nonexistent")).thenThrow(new RuntimeException("User not found"));

        // When & Then
        mockMvc.perform(get("/api/posts?username=nonexistent"))
                .andExpect(status().isInternalServerError());

        verify(postService).getPostsByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should handle service exception for get posts by multiple subreddits")
    void shouldHandleServiceExceptionForGetPostsByMultipleSubreddits() throws Exception {
        // Given
        List<String> subredditNames = List.of("nonexistent");
        when(postService.getPostsByMultipleSubreddits(subredditNames)).thenThrow(new RuntimeException("Subreddit not found"));

        // When & Then
        mockMvc.perform(get("/api/posts/by-subreddits")
                .param("subredditNames", "nonexistent"))
                .andExpect(status().isInternalServerError());

        verify(postService).getPostsByMultipleSubreddits(subredditNames);
    }
} 