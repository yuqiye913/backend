package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceValidationTest {

    @Mock
    private com.programming.techie.springredditclone.repository.PostRepository postRepository;
    
    @Mock
    private com.programming.techie.springredditclone.repository.SubredditRepository subredditRepository;
    
    @Mock
    private com.programming.techie.springredditclone.repository.UserRepository userRepository;
    
    @Mock
    private com.programming.techie.springredditclone.service.AuthService authService;
    
    @Mock
    private com.programming.techie.springredditclone.service.BlockService blockService;
    
    @Mock
    private com.programming.techie.springredditclone.mapper.PostMapper postMapper;
    
    @Mock
    private com.programming.techie.springredditclone.util.CursorUtil cursorUtil;
    
    @Mock
    private com.programming.techie.springredditclone.repository.VoteRepository voteRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private com.programming.techie.springredditclone.model.User testUser;
    private com.programming.techie.springredditclone.model.Subreddit testSubreddit;

    @BeforeEach
    void setUp() {
        testUser = new com.programming.techie.springredditclone.model.User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        
        testSubreddit = new com.programming.techie.springredditclone.model.Subreddit();
        testSubreddit.setId(1L);
        testSubreddit.setName("testsubreddit");
        
        lenient().when(authService.getCurrentUser()).thenReturn(testUser);
        lenient().when(subredditRepository.findByName(anyString())).thenReturn(java.util.Optional.of(testSubreddit));
        lenient().when(subredditRepository.save(any())).thenReturn(testSubreddit);
        lenient().when(postRepository.save(any())).thenReturn(new com.programming.techie.springredditclone.model.Post());
    }

    @Test
    void shouldThrowExceptionWhenSubredditNamesIsNull() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(null);
        postRequest.setPostName("Test Post");
        postRequest.setDescription("Test content");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("At least one subreddit must be specified", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSubredditNamesIsEmpty() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Collections.emptyList());
        postRequest.setPostName("Test Post");
        postRequest.setDescription("Test content");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("At least one subreddit must be specified", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPostNameIsNull() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName(null);
        postRequest.setDescription("Test content");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("Post name cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPostNameIsEmpty() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("");
        postRequest.setDescription("Test content");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("Post name cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPostNameIsOnlyWhitespace() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("   ");
        postRequest.setDescription("Test content");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("Post name cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("Test Post");
        postRequest.setDescription(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("Post content/description cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("Test Post");
        postRequest.setDescription("");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("Post content/description cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsOnlyWhitespace() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("Test Post");
        postRequest.setDescription("   ");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("Post content/description cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsOnlyHashtag() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("Test Post");
        postRequest.setDescription("#something");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("Post content cannot consist only of hashtags", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsOnlyMultipleHashtags() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("Test Post");
        postRequest.setDescription("#something #another #test");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("Post content cannot consist only of hashtags", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsOnlySpecialCharacters() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("Test Post");
        postRequest.setDescription("!@#$%^&*()");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.save(postRequest);
        });

        assertEquals("Post content must contain meaningful text beyond hashtags", exception.getMessage());
    }

    @Test
    void shouldAcceptValidPostWithHashtagsAndText() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("Test Post");
        postRequest.setDescription("This is a valid post with #hashtags and real content");

        assertDoesNotThrow(() -> {
            postService.save(postRequest);
        });

        verify(postRepository).save(any());
    }

    @Test
    void shouldAcceptValidPostWithOnlyText() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("Test Post");
        postRequest.setDescription("This is a valid post with only text content");

        assertDoesNotThrow(() -> {
            postService.save(postRequest);
        });

        verify(postRepository).save(any());
    }

    @Test
    void shouldAcceptValidPostWithMixedContent() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditNames(Arrays.asList("testsubreddit"));
        postRequest.setPostName("Test Post");
        postRequest.setDescription("Check out this cool post! #awesome #cool #test");

        assertDoesNotThrow(() -> {
            postService.save(postRequest);
        });

        verify(postRepository).save(any());
    }
} 