package com.programming.techie.springredditclone.service.content;

import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.dto.PostResponse;
import com.programming.techie.springredditclone.mapper.PostMapper;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.Subreddit;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.SubredditRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private SubredditRepository subredditRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    private User testUser;
    private Subreddit programmingSubreddit;
    private Subreddit gamingSubreddit;
    private PostRequest postRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setCreated(Instant.now());
        testUser.setEnabled(true);

        // Create test subreddits
        programmingSubreddit = new Subreddit();
        programmingSubreddit.setId(1L);
        programmingSubreddit.setName("programming");
        programmingSubreddit.setDescription("Programming discussions");

        gamingSubreddit = new Subreddit();
        gamingSubreddit.setId(2L);
        gamingSubreddit.setName("gaming");
        gamingSubreddit.setDescription("Gaming discussions");

        // Create test post request
        postRequest = new PostRequest();
        postRequest.setPostName("Test Post");
        postRequest.setDescription("Test Description");
        postRequest.setUrl("http://example.com");
        postRequest.setSubredditNames(List.of("programming", "gaming"));
    }

    @Test
    @DisplayName("Should successfully save post with multiple subreddits")
    void shouldSavePostWithMultipleSubreddits() {
        // Given
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(subredditRepository.findByName("programming")).thenReturn(Optional.of(programmingSubreddit));
        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(gamingSubreddit));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        postService.save(postRequest);

        // Then
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertThat(savedPost.getPostName()).isEqualTo("Test Post");
        assertThat(savedPost.getDescription()).isEqualTo("Test Description");
        assertThat(savedPost.getUrl()).isEqualTo("http://example.com");
        assertThat(savedPost.getUser()).isEqualTo(testUser);
        assertThat(savedPost.getVoteCount()).isEqualTo(0);
        assertThat(savedPost.getCreatedDate()).isNotNull();
        assertThat(savedPost.getSubreddits()).hasSize(2);
        assertThat(savedPost.getSubreddits()).contains(programmingSubreddit, gamingSubreddit);
    }

    @Test
    @DisplayName("Should successfully save post with single subreddit")
    void shouldSavePostWithSingleSubreddit() {
        // Given
        postRequest.setSubredditNames(List.of("programming"));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(subredditRepository.findByName("programming")).thenReturn(Optional.of(programmingSubreddit));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        postService.save(postRequest);

        // Then
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertThat(savedPost.getSubreddits()).hasSize(1);
        assertThat(savedPost.getSubreddits()).contains(programmingSubreddit);
    }

    @Test
    @DisplayName("Should throw exception when no subreddits specified")
    void shouldThrowExceptionWhenNoSubredditsSpecified() {
        // Given
        postRequest.setSubredditNames(null);

        // When & Then
        assertThatThrownBy(() -> postService.save(postRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("At least one subreddit must be specified");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw exception when empty subreddit list")
    void shouldThrowExceptionWhenEmptySubredditList() {
        // Given
        postRequest.setSubredditNames(List.of());

        // When & Then
        assertThatThrownBy(() -> postService.save(postRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("At least one subreddit must be specified");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw exception when subreddit not found")
    void shouldThrowExceptionWhenSubredditNotFound() {
        // Given
        when(subredditRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> {
            postRequest.setSubredditNames(List.of("nonexistent"));
            postService.save(postRequest);
        })
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subreddit not found with name - nonexistent");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw exception when one subreddit not found in multiple")
    void shouldThrowExceptionWhenOneSubredditNotFoundInMultiple() {
        // Given
        when(subredditRepository.findByName("programming")).thenReturn(Optional.of(programmingSubreddit));
        when(subredditRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> {
            postRequest.setSubredditNames(List.of("programming", "nonexistent"));
            postService.save(postRequest);
        })
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subreddit not found with name - nonexistent");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should get post by ID successfully")
    void shouldGetPostById() {
        // Given
        Post post = new Post();
        post.setPostId(1L);
        post.setPostName("Test Post");
        post.setSubreddits(Set.of(programmingSubreddit));

        PostResponse expectedResponse = new PostResponse();
        expectedResponse.setId(1L);
        expectedResponse.setPostName("Test Post");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.mapToDto(post)).thenReturn(expectedResponse);

        // When
        PostResponse result = postService.getPost(1L);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(postRepository).findById(1L);
        verify(postMapper).mapToDto(post);
    }

    @Test
    @DisplayName("Should throw exception when post not found")
    void shouldThrowExceptionWhenPostNotFound() {
        // Given
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.getPost(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Post not found with id - 999");
    }

    @Test
    @DisplayName("Should get posts by multiple subreddits successfully")
    void shouldGetPostsByMultipleSubreddits() {
        // Given
        List<String> subredditNames = List.of("programming", "gaming");
        Set<Subreddit> subreddits = Set.of(programmingSubreddit, gamingSubreddit);
        List<Post> posts = List.of(new Post(), new Post());
        List<PostResponse> expectedResponses = List.of(new PostResponse(), new PostResponse());

        when(subredditRepository.findByName("programming")).thenReturn(Optional.of(programmingSubreddit));
        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(gamingSubreddit));
        when(postRepository.findBySubredditsIn(subreddits)).thenReturn(posts);
        when(postMapper.mapToDto(posts.get(0))).thenReturn(expectedResponses.get(0));
        when(postMapper.mapToDto(posts.get(1))).thenReturn(expectedResponses.get(1));

        // When
        List<PostResponse> result = postService.getPostsByMultipleSubreddits(subredditNames);

        // Then
        assertThat(result).isEqualTo(expectedResponses);
        verify(postRepository).findBySubredditsIn(subreddits);
    }

    @Test
    @DisplayName("Should get posts by subreddit ID successfully")
    void shouldGetPostsBySubredditId() {
        // Given
        List<Post> posts = List.of(new Post(), new Post());
        List<PostResponse> expectedResponses = List.of(new PostResponse(), new PostResponse());

        when(subredditRepository.findById(1L)).thenReturn(Optional.of(programmingSubreddit));
        when(postRepository.findBySubredditsContaining(programmingSubreddit)).thenReturn(posts);
        when(postMapper.mapToDto(posts.get(0))).thenReturn(expectedResponses.get(0));
        when(postMapper.mapToDto(posts.get(1))).thenReturn(expectedResponses.get(1));

        // When
        List<PostResponse> result = postService.getPostsBySubreddit(1L);

        // Then
        assertThat(result).isEqualTo(expectedResponses);
        verify(postRepository).findBySubredditsContaining(programmingSubreddit);
    }

    @Test
    @DisplayName("Should get posts by username successfully")
    void shouldGetPostsByUsername() {
        // Given
        List<Post> posts = List.of(new Post(), new Post());
        List<PostResponse> expectedResponses = List.of(new PostResponse(), new PostResponse());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findByUser(testUser)).thenReturn(posts);
        when(postMapper.mapToDto(posts.get(0))).thenReturn(expectedResponses.get(0));
        when(postMapper.mapToDto(posts.get(1))).thenReturn(expectedResponses.get(1));

        // When
        List<PostResponse> result = postService.getPostsByUsername("testuser");

        // Then
        assertThat(result).isEqualTo(expectedResponses);
        verify(postRepository).findByUser(testUser);
    }

    @Test
    @DisplayName("Should update post successfully")
    void shouldUpdatePost() {
        // Given
        Long postId = 1L;
        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setPostName("Old Post Name");
        existingPost.setDescription("Old Description");
        existingPost.setUrl("http://old.com");
        existingPost.setUser(testUser);
        existingPost.setSubreddits(Set.of(programmingSubreddit));

        PostRequest updateRequest = new PostRequest();
        updateRequest.setPostName("Updated Post Name");
        updateRequest.setDescription("Updated Description");
        updateRequest.setUrl("http://updated.com");
        updateRequest.setSubredditNames(List.of("programming", "gaming"));

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(subredditRepository.findByName("programming")).thenReturn(Optional.of(programmingSubreddit));
        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(gamingSubreddit));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        postService.updatePost(postId, updateRequest);

        // Then
        verify(postRepository).findById(postId);
        verify(authService).getCurrentUser();
        verify(postRepository).save(postCaptor.capture());
        
        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getPostName()).isEqualTo("Updated Post Name");
        assertThat(savedPost.getDescription()).isEqualTo("Updated Description");
        assertThat(savedPost.getUrl()).isEqualTo("http://updated.com");
        assertThat(savedPost.getSubreddits()).hasSize(2);
        assertThat(savedPost.getSubreddits()).contains(programmingSubreddit, gamingSubreddit);
    }

    @Test
    @DisplayName("Should throw exception when updating post not found")
    void shouldThrowExceptionWhenUpdatingPostNotFound() {
        // Given
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(postId, postRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Post not found with id - 999");
    }

    @Test
    @DisplayName("Should throw exception when updating post not owned by user")
    void shouldThrowExceptionWhenUpdatingPostNotOwnedByUser() {
        // Given
        Long postId = 1L;
        User otherUser = new User();
        otherUser.setUserId(2L);
        otherUser.setUsername("otheruser");

        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setUser(otherUser);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(authService.getCurrentUser()).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(postId, postRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You can only update your own posts");
    }

    @Test
    @DisplayName("Should delete post successfully")
    void shouldDeletePost() {
        // Given
        Long postId = 1L;
        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setUser(testUser);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(authService.getCurrentUser()).thenReturn(testUser);

        // When
        postService.deletePost(postId);

        // Then
        verify(postRepository).findById(postId);
        verify(authService).getCurrentUser();
        verify(postRepository).delete(existingPost);
    }

    @Test
    @DisplayName("Should throw exception when deleting post not found")
    void shouldThrowExceptionWhenDeletingPostNotFound() {
        // Given
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.deletePost(postId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Post not found with id - 999");
    }

    @Test
    @DisplayName("Should throw exception when deleting post not owned by user")
    void shouldThrowExceptionWhenDeletingPostNotOwnedByUser() {
        // Given
        Long postId = 1L;
        User otherUser = new User();
        otherUser.setUserId(2L);
        otherUser.setUsername("otheruser");

        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setUser(otherUser);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(authService.getCurrentUser()).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> postService.deletePost(postId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You can only delete your own posts");
    }
} 