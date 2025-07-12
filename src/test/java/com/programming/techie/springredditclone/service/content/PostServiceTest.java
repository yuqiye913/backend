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
import com.programming.techie.springredditclone.repository.VoteRepository;
import com.programming.techie.springredditclone.model.Vote;
import com.programming.techie.springredditclone.model.VoteType;
import com.programming.techie.springredditclone.service.BlockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.programming.techie.springredditclone.dto.CursorPageResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private BlockService blockService;

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
    @DisplayName("Should create new subreddit when it doesn't exist")
    void shouldCreateNewSubredditWhenItDoesntExist() {
        // Given
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(subredditRepository.findByName("newsubreddit")).thenReturn(Optional.empty());
        
        Subreddit newSubreddit = new Subreddit();
        newSubreddit.setId(3L);
        newSubreddit.setName("newsubreddit");
        newSubreddit.setDescription("Created by " + testUser.getUsername());
        newSubreddit.setUser(testUser);
        newSubreddit.setCreatedDate(Instant.now());
        
        when(subredditRepository.save(any(Subreddit.class))).thenReturn(newSubreddit);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postRequest.setSubredditNames(List.of("newsubreddit"));

        // When
        postService.save(postRequest);

        // Then
        verify(subredditRepository).findByName("newsubreddit");
        verify(subredditRepository).save(any(Subreddit.class));
        verify(postRepository).save(postCaptor.capture());
        
        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getSubreddits()).hasSize(1);
        assertThat(savedPost.getSubreddits().iterator().next().getName()).isEqualTo("newsubreddit");
    }

    @Test
    @DisplayName("Should create multiple new subreddits when they don't exist")
    void shouldCreateMultipleNewSubredditsWhenTheyDontExist() {
        // Given
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(subredditRepository.findByName("newsub1")).thenReturn(Optional.empty());
        when(subredditRepository.findByName("newsub2")).thenReturn(Optional.empty());
        
        Subreddit newSub1 = new Subreddit();
        newSub1.setId(3L);
        newSub1.setName("newsub1");
        newSub1.setDescription("Created by " + testUser.getUsername());
        newSub1.setUser(testUser);
        newSub1.setCreatedDate(Instant.now());
        
        Subreddit newSub2 = new Subreddit();
        newSub2.setId(4L);
        newSub2.setName("newsub2");
        newSub2.setDescription("Created by " + testUser.getUsername());
        newSub2.setUser(testUser);
        newSub2.setCreatedDate(Instant.now());
        
        when(subredditRepository.save(any(Subreddit.class)))
                .thenReturn(newSub1)
                .thenReturn(newSub2);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postRequest.setSubredditNames(List.of("newsub1", "newsub2"));

        // When
        postService.save(postRequest);

        // Then
        verify(subredditRepository).findByName("newsub1");
        verify(subredditRepository).findByName("newsub2");
        verify(subredditRepository, times(2)).save(any(Subreddit.class));
        verify(postRepository).save(postCaptor.capture());
        
        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getSubreddits()).hasSize(2);
        assertThat(savedPost.getSubreddits()).extracting("name").containsExactlyInAnyOrder("newsub1", "newsub2");
    }

    @Test
    @DisplayName("Should use existing subreddit and create new one")
    void shouldUseExistingSubredditAndCreateNewOne() {
        // Given
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(subredditRepository.findByName("programming")).thenReturn(Optional.of(programmingSubreddit));
        when(subredditRepository.findByName("newsubreddit")).thenReturn(Optional.empty());
        
        Subreddit newSubreddit = new Subreddit();
        newSubreddit.setId(3L);
        newSubreddit.setName("newsubreddit");
        newSubreddit.setDescription("Created by " + testUser.getUsername());
        newSubreddit.setUser(testUser);
        newSubreddit.setCreatedDate(Instant.now());
        
        when(subredditRepository.save(any(Subreddit.class))).thenReturn(newSubreddit);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postRequest.setSubredditNames(List.of("programming", "newsubreddit"));

        // When
        postService.save(postRequest);

        // Then
        verify(subredditRepository).findByName("programming");
        verify(subredditRepository).findByName("newsubreddit");
        verify(subredditRepository).save(any(Subreddit.class));
        verify(postRepository).save(postCaptor.capture());
        
        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getSubreddits()).hasSize(2);
        assertThat(savedPost.getSubreddits()).contains(programmingSubreddit);
        assertThat(savedPost.getSubreddits()).extracting("name").contains("newsubreddit");
    }

    @Test
    @DisplayName("Should throw exception when subreddit name is empty")
    void shouldThrowExceptionWhenSubredditNameIsEmpty() {
        // Given
        postRequest.setSubredditNames(List.of(""));

        // When & Then
        assertThatThrownBy(() -> postService.save(postRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subreddit name cannot be empty");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw exception when subreddit name is null")
    void shouldThrowExceptionWhenSubredditNameIsNull() {
        // Given
        List<String> subredditNames = new ArrayList<>();
        subredditNames.add(null);
        postRequest.setSubredditNames(subredditNames);

        // When & Then
        assertThatThrownBy(() -> postService.save(postRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subreddit name cannot be empty");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw exception when subreddit name is whitespace only")
    void shouldThrowExceptionWhenSubredditNameIsWhitespaceOnly() {
        // Given
        postRequest.setSubredditNames(List.of("   "));

        // When & Then
        assertThatThrownBy(() -> postService.save(postRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subreddit name cannot be empty");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should get post by ID successfully without vote status when not authenticated")
    void shouldGetPostByIdWithoutVoteStatusWhenNotAuthenticated() {
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
        when(authService.isLoggedIn()).thenReturn(false);

        // When
        PostResponse result = postService.getPost(1L);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.isUpVote()).isFalse();
        assertThat(result.isDownVote()).isFalse();
        verify(postRepository).findById(1L);
        verify(postMapper).mapToDto(post);
        verify(authService).isLoggedIn();
        verify(authService, never()).getCurrentUser();
    }

    @Test
    @DisplayName("Should get post by ID successfully with vote status when authenticated")
    void shouldGetPostByIdWithVoteStatusWhenAuthenticated() {
        // Given
        Post post = new Post();
        post.setPostId(1L);
        post.setPostName("Test Post");
        post.setSubreddits(Set.of(programmingSubreddit));

        PostResponse expectedResponse = new PostResponse();
        expectedResponse.setId(1L);
        expectedResponse.setPostName("Test Post");

        Vote upvote = new Vote();
        upvote.setVoteType(VoteType.UPVOTE);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.mapToDto(post)).thenReturn(expectedResponse);
        when(authService.isLoggedIn()).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(post, testUser)).thenReturn(Optional.of(upvote));

        // When
        PostResponse result = postService.getPost(1L);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.isUpVote()).isTrue();
        assertThat(result.isDownVote()).isFalse();
        verify(postRepository).findById(1L);
        verify(postMapper).mapToDto(post);
        verify(authService).isLoggedIn();
        verify(authService).getCurrentUser();
        verify(voteRepository, times(2)).findByPostAndUser(post, testUser); // Called twice: once for upvote, once for downvote
    }

    @Test
    @DisplayName("Should get post by ID successfully with no vote status when authenticated but no vote")
    void shouldGetPostByIdWithNoVoteStatusWhenAuthenticatedButNoVote() {
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
        when(authService.isLoggedIn()).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(voteRepository.findByPostAndUser(post, testUser)).thenReturn(Optional.empty());

        // When
        PostResponse result = postService.getPost(1L);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.isUpVote()).isFalse();
        assertThat(result.isDownVote()).isFalse();
        verify(postRepository).findById(1L);
        verify(postMapper).mapToDto(post);
        verify(authService).isLoggedIn();
        verify(authService).getCurrentUser();
        verify(voteRepository, times(2)).findByPostAndUser(post, testUser); // Called twice: once for upvote, once for downvote
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
    @DisplayName("Should filter out non-existent subreddits when getting posts by multiple subreddits")
    void shouldFilterOutNonExistentSubredditsWhenGettingPostsByMultipleSubreddits() {
        // Given
        List<String> subredditNames = List.of("programming", "nonexistent", "gaming");
        Set<Subreddit> existingSubreddits = Set.of(programmingSubreddit, gamingSubreddit);
        List<Post> posts = List.of(new Post(), new Post());
        List<PostResponse> expectedResponses = List.of(new PostResponse(), new PostResponse());

        when(subredditRepository.findByName("programming")).thenReturn(Optional.of(programmingSubreddit));
        when(subredditRepository.findByName("nonexistent")).thenReturn(Optional.empty());
        when(subredditRepository.findByName("gaming")).thenReturn(Optional.of(gamingSubreddit));
        when(postRepository.findBySubredditsIn(existingSubreddits)).thenReturn(posts);
        when(postMapper.mapToDto(posts.get(0))).thenReturn(expectedResponses.get(0));
        when(postMapper.mapToDto(posts.get(1))).thenReturn(expectedResponses.get(1));

        // When
        List<PostResponse> result = postService.getPostsByMultipleSubreddits(subredditNames);

        // Then
        assertThat(result).isEqualTo(expectedResponses);
        verify(postRepository).findBySubredditsIn(existingSubreddits);
    }

    @Test
    @DisplayName("Should return empty list when all subreddits are non-existent")
    void shouldReturnEmptyListWhenAllSubredditsAreNonExistent() {
        // Given
        List<String> subredditNames = List.of("nonexistent1", "nonexistent2");

        when(subredditRepository.findByName("nonexistent1")).thenReturn(Optional.empty());
        when(subredditRepository.findByName("nonexistent2")).thenReturn(Optional.empty());

        // When
        List<PostResponse> result = postService.getPostsByMultipleSubreddits(subredditNames);

        // Then
        assertThat(result).isEmpty();
        verify(postRepository, never()).findBySubredditsIn(any());
    }

    @Test
    @DisplayName("Should get posts by subreddit ID successfully")
    void shouldGetPostsBySubredditId() {
        // Given
        List<Post> posts = List.of(new Post(), new Post());
        List<PostResponse> expectedResponses = List.of(new PostResponse(), new PostResponse());

        when(subredditRepository.findById(1L)).thenReturn(Optional.of(programmingSubreddit));
        when(postRepository.findBySubredditWithCursor(eq(programmingSubreddit), any(Instant.class), any(Long.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(posts);
        when(postMapper.mapToDto(posts.get(0))).thenReturn(expectedResponses.get(0));
        when(postMapper.mapToDto(posts.get(1))).thenReturn(expectedResponses.get(1));

        // When
        CursorPageResponse<PostResponse> result = postService.getPostsBySubreddit(1L, null, 10);

        // Then
        assertThat(result.getContent()).isEqualTo(expectedResponses);
        verify(postRepository).findBySubredditWithCursor(eq(programmingSubreddit), any(Instant.class), any(Long.class), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    @DisplayName("Should get posts by username successfully")
    void shouldGetPostsByUsername() {
        // Given
        List<Post> posts = List.of(new Post(), new Post());
        List<PostResponse> expectedResponses = List.of(new PostResponse(), new PostResponse());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findByUserWithCursor(eq(testUser), any(Instant.class), any(Long.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(posts);
        when(postMapper.mapToDto(posts.get(0))).thenReturn(expectedResponses.get(0));
        when(postMapper.mapToDto(posts.get(1))).thenReturn(expectedResponses.get(1));

        // When
        CursorPageResponse<PostResponse> result = postService.getPostsByUsername("testuser", null, 10);

        // Then
        assertThat(result.getContent()).isEqualTo(expectedResponses);
        verify(postRepository).findByUserWithCursor(eq(testUser), any(Instant.class), any(Long.class), any(org.springframework.data.domain.Pageable.class));
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
        verify(authService, times(2)).getCurrentUser(); // Called twice: once in updatePost, once in validateAndGetSubreddits
        verify(postRepository).save(postCaptor.capture());
        
        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getPostName()).isEqualTo("Updated Post Name");
        assertThat(savedPost.getDescription()).isEqualTo("Updated Description");
        assertThat(savedPost.getUrl()).isEqualTo("http://updated.com");
        assertThat(savedPost.getSubreddits()).hasSize(2);
        assertThat(savedPost.getSubreddits()).contains(programmingSubreddit, gamingSubreddit);
    }

    @Test
    @DisplayName("Should update post with new subreddit creation")
    void shouldUpdatePostWithNewSubredditCreation() {
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
        updateRequest.setSubredditNames(List.of("programming", "newsubreddit"));

        Subreddit newSubreddit = new Subreddit();
        newSubreddit.setId(3L);
        newSubreddit.setName("newsubreddit");
        newSubreddit.setDescription("Created by " + testUser.getUsername());
        newSubreddit.setUser(testUser);
        newSubreddit.setCreatedDate(Instant.now());

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(subredditRepository.findByName("programming")).thenReturn(Optional.of(programmingSubreddit));
        when(subredditRepository.findByName("newsubreddit")).thenReturn(Optional.empty());
        when(subredditRepository.save(any(Subreddit.class))).thenReturn(newSubreddit);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        postService.updatePost(postId, updateRequest);

        // Then
        verify(postRepository).findById(postId);
        verify(authService, times(2)).getCurrentUser(); // Called twice: once in updatePost, once in validateAndGetSubreddits
        verify(subredditRepository).findByName("programming");
        verify(subredditRepository).findByName("newsubreddit");
        verify(subredditRepository).save(any(Subreddit.class));
        verify(postRepository).save(postCaptor.capture());
        
        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getPostName()).isEqualTo("Updated Post Name");
        assertThat(savedPost.getDescription()).isEqualTo("Updated Description");
        assertThat(savedPost.getUrl()).isEqualTo("http://updated.com");
        assertThat(savedPost.getSubreddits()).hasSize(2);
        assertThat(savedPost.getSubreddits()).contains(programmingSubreddit);
        assertThat(savedPost.getSubreddits()).extracting("name").contains("newsubreddit");
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