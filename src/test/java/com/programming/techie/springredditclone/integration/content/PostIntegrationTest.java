package com.programming.techie.springredditclone.integration.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.config.TestSecurityConfig;
import com.programming.techie.springredditclone.config.TestAuthConfig;
import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.Subreddit;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.SubredditRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@Import({TestSecurityConfig.class, TestAuthConfig.class})
@ActiveProfiles("test")
@Transactional
class PostIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubredditRepository subredditRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;
    private Subreddit programmingSubreddit;
    private Subreddit gamingSubreddit;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create test user
        testUser = new User();
        testUser.setUsername("integrationuser");
        testUser.setEmail("integration@example.com");
        testUser.setPassword("password");
        testUser.setCreated(Instant.now());
        testUser.setEnabled(true);
        testUser = userRepository.save(testUser);

        // Create test subreddits
        programmingSubreddit = new Subreddit();
        programmingSubreddit.setName("programming");
        programmingSubreddit.setDescription("Programming discussions");
        programmingSubreddit.setCreatedDate(Instant.now());
        programmingSubreddit.setUser(testUser);
        programmingSubreddit = subredditRepository.save(programmingSubreddit);

        gamingSubreddit = new Subreddit();
        gamingSubreddit.setName("gaming");
        gamingSubreddit.setDescription("Gaming discussions");
        gamingSubreddit.setCreatedDate(Instant.now());
        gamingSubreddit.setUser(testUser);
        gamingSubreddit = subredditRepository.save(gamingSubreddit);
    }

    @Test
    @DisplayName("Should create post with multiple subreddits successfully")
    void shouldCreatePostWithMultipleSubreddits() throws Exception {
        // Given
        PostRequest postRequest = new PostRequest();
        postRequest.setPostName("Integration Test Post");
        postRequest.setDescription("This is a test post created via integration test");
        postRequest.setUrl("http://example.com/test");
        postRequest.setSubredditNames(List.of("programming", "gaming"));

        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());

        // Verify post was created in database
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(1);
        
        Post savedPost = posts.get(0);
        assertThat(savedPost.getPostName()).isEqualTo("Integration Test Post");
        assertThat(savedPost.getSubreddits()).hasSize(2);
        assertThat(savedPost.getSubreddits()).anyMatch(sub -> sub.getName().equals("programming"));
        assertThat(savedPost.getSubreddits()).anyMatch(sub -> sub.getName().equals("gaming"));
    }

    @Test
    @DisplayName("Should get all posts successfully")
    void shouldGetAllPosts() throws Exception {
        // Given - Create a test post
        Post post = new Post();
        post.setPostName("Test Post");
        post.setDescription("Test Description");
        post.setUrl("http://example.com");
        post.setUser(testUser);
        post.setCreatedDate(Instant.now());
        post.setVoteCount(0);
        post.setSubreddits(Set.of(programmingSubreddit));
        postRepository.save(post);

        // When & Then
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get posts by subreddit ID successfully")
    void shouldGetPostsBySubredditId() throws Exception {
        // Given - Create a test post
        Post post = new Post();
        post.setPostName("Test Post");
        post.setDescription("Test Description");
        post.setUrl("http://example.com");
        post.setUser(testUser);
        post.setCreatedDate(Instant.now());
        post.setVoteCount(0);
        post.setSubreddits(Set.of(programmingSubreddit));
        postRepository.save(post);

        // When & Then
        mockMvc.perform(get("/api/posts?subredditId=" + programmingSubreddit.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get posts by multiple subreddits successfully")
    void shouldGetPostsByMultipleSubreddits() throws Exception {
        // Given - Create test posts
        Post post1 = new Post();
        post1.setPostName("Programming Post");
        post1.setDescription("Programming content");
        post1.setUrl("http://example.com/1");
        post1.setUser(testUser);
        post1.setCreatedDate(Instant.now());
        post1.setVoteCount(0);
        post1.setSubreddits(Set.of(programmingSubreddit));
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setPostName("Gaming Post");
        post2.setDescription("Gaming content");
        post2.setUrl("http://example.com/2");
        post2.setUser(testUser);
        post2.setCreatedDate(Instant.now());
        post2.setVoteCount(0);
        post2.setSubreddits(Set.of(gamingSubreddit));
        postRepository.save(post2);

        // When & Then
        mockMvc.perform(get("/api/posts/by-subreddits")
                .param("subredditNames", "programming", "gaming"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 400 when subreddit names are missing")
    void shouldReturnBadRequestWhenSubredditNamesMissing() throws Exception {
        // Given
        PostRequest postRequest = new PostRequest();
        postRequest.setPostName("Test Post");
        postRequest.setDescription("Test Description");
        postRequest.setSubredditNames(null); // Missing subreddit names

        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when post name is missing")
    void shouldReturnBadRequestWhenPostNameMissing() throws Exception {
        // Given
        PostRequest postRequest = new PostRequest();
        postRequest.setPostName(null); // Missing post name
        postRequest.setDescription("Test Description");
        postRequest.setSubredditNames(List.of("programming"));

        // When & Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }
} 