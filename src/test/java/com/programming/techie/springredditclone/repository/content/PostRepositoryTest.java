package com.programming.techie.springredditclone.repository.content;

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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubredditRepository subredditRepository;

    private User user1;
    private User user2;
    private Subreddit programmingSubreddit;
    private Subreddit gamingSubreddit;
    private Subreddit technologySubreddit;
    private Post post1;
    private Post post2;
    private Post post3;

    @BeforeEach
    void setUp() {
        // Create test users
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        user1.setCreated(Instant.now());
        user1.setEnabled(true);

        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setCreated(Instant.now());
        user2.setEnabled(true);

        // Save users
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        // Create test subreddits
        programmingSubreddit = new Subreddit();
        programmingSubreddit.setName("programming");
        programmingSubreddit.setDescription("Programming discussions");
        programmingSubreddit.setCreatedDate(Instant.now());
        programmingSubreddit.setUser(user1);

        gamingSubreddit = new Subreddit();
        gamingSubreddit.setName("gaming");
        gamingSubreddit.setDescription("Gaming discussions");
        gamingSubreddit.setCreatedDate(Instant.now());
        gamingSubreddit.setUser(user1);

        technologySubreddit = new Subreddit();
        technologySubreddit.setName("technology");
        technologySubreddit.setDescription("Technology discussions");
        technologySubreddit.setCreatedDate(Instant.now());
        technologySubreddit.setUser(user2);

        // Save subreddits
        programmingSubreddit = subredditRepository.save(programmingSubreddit);
        gamingSubreddit = subredditRepository.save(gamingSubreddit);
        technologySubreddit = subredditRepository.save(technologySubreddit);

        // Create test posts
        post1 = new Post();
        post1.setPostName("Programming Post");
        post1.setDescription("A post about programming");
        post1.setUrl("http://example.com/1");
        post1.setUser(user1);
        post1.setCreatedDate(Instant.now());
        post1.setVoteCount(5);
        post1.setSubreddits(new HashSet<>(Set.of(programmingSubreddit, technologySubreddit)));

        post2 = new Post();
        post2.setPostName("Gaming Post");
        post2.setDescription("A post about gaming");
        post2.setUrl("http://example.com/2");
        post2.setUser(user2);
        post2.setCreatedDate(Instant.now());
        post2.setVoteCount(3);
        post2.setSubreddits(new HashSet<>(Set.of(gamingSubreddit)));

        post3 = new Post();
        post3.setPostName("Tech Post");
        post3.setDescription("A post about technology");
        post3.setUrl("http://example.com/3");
        post3.setUser(user1);
        post3.setCreatedDate(Instant.now());
        post3.setVoteCount(7);
        post3.setSubreddits(new HashSet<>(Set.of(technologySubreddit, programmingSubreddit)));

        // Save posts
        post1 = postRepository.save(post1);
        post2 = postRepository.save(post2);
        post3 = postRepository.save(post3);
    }

    @Test
    @DisplayName("Should save post with multiple subreddits")
    void shouldSavePostWithMultipleSubreddits() {
        // Given
        Post newPost = new Post();
        newPost.setPostName("New Post");
        newPost.setDescription("A new post with multiple subreddits");
        newPost.setUrl("http://example.com/new");
        newPost.setUser(user1);
        newPost.setCreatedDate(Instant.now());
        newPost.setVoteCount(0);
        newPost.setSubreddits(Set.of(programmingSubreddit, gamingSubreddit));

        // When
        Post savedPost = postRepository.save(newPost);

        // Then
        assertThat(savedPost.getPostId()).isNotNull();
        assertThat(savedPost.getPostName()).isEqualTo("New Post");
        assertThat(savedPost.getSubreddits()).hasSize(2);
        assertThat(savedPost.getSubreddits()).contains(programmingSubreddit, gamingSubreddit);
    }

    @Test
    @DisplayName("Should find posts by user")
    void shouldFindPostsByUser() {
        // When
        List<Post> user1Posts = postRepository.findByUser(user1);
        List<Post> user2Posts = postRepository.findByUser(user2);

        // Then
        assertThat(user1Posts).hasSize(2); // post1 and post3
        assertThat(user2Posts).hasSize(1); // post2
        
        assertThat(user1Posts).allMatch(post -> post.getUser().equals(user1));
        assertThat(user2Posts).allMatch(post -> post.getUser().equals(user2));
    }

    @Test
    @DisplayName("Should find posts by subreddit")
    void shouldFindPostsBySubreddit() {
        // When
        List<Post> programmingPosts = postRepository.findBySubredditsContaining(programmingSubreddit);
        List<Post> gamingPosts = postRepository.findBySubredditsContaining(gamingSubreddit);
        List<Post> technologyPosts = postRepository.findBySubredditsContaining(technologySubreddit);

        // Then
        assertThat(programmingPosts).hasSize(2); // post1 and post3
        assertThat(gamingPosts).hasSize(1); // post2
        assertThat(technologyPosts).hasSize(2); // post1 and post3
        
        assertThat(programmingPosts).allMatch(post -> post.getSubreddits().contains(programmingSubreddit));
        assertThat(gamingPosts).allMatch(post -> post.getSubreddits().contains(gamingSubreddit));
        assertThat(technologyPosts).allMatch(post -> post.getSubreddits().contains(technologySubreddit));
    }

    @Test
    @DisplayName("Should find posts by multiple subreddits")
    void shouldFindPostsByMultipleSubreddits() {
        // When
        Set<Subreddit> searchSubreddits = Set.of(programmingSubreddit, gamingSubreddit);
        List<Post> posts = postRepository.findBySubredditsIn(searchSubreddits);

        // Then
        assertThat(posts).hasSize(3); // All posts since they all contain at least one of the search subreddits
        assertThat(posts).anyMatch(post -> post.getSubreddits().contains(programmingSubreddit));
        assertThat(posts).anyMatch(post -> post.getSubreddits().contains(gamingSubreddit));
    }

    @Test
    @DisplayName("Should find posts by specific subreddit set")
    void shouldFindPostsBySpecificSubredditSet() {
        // When
        Set<Subreddit> searchSubreddits = Set.of(technologySubreddit);
        List<Post> posts = postRepository.findBySubredditsIn(searchSubreddits);

        // Then
        assertThat(posts).hasSize(2); // post1 and post3
        assertThat(posts).allMatch(post -> post.getSubreddits().contains(technologySubreddit));
    }

    @Test
    @DisplayName("Should update post subreddits")
    void shouldUpdatePostSubreddits() {
        // Given
        post1.setSubreddits(new HashSet<>(Set.of(programmingSubreddit, gamingSubreddit, technologySubreddit)));

        // When
        Post updatedPost = postRepository.save(post1);

        // Then
        assertThat(updatedPost.getSubreddits()).hasSize(3);
        assertThat(updatedPost.getSubreddits()).contains(programmingSubreddit, gamingSubreddit, technologySubreddit);
    }

    @Test
    @DisplayName("Should remove subreddit from post")
    void shouldRemoveSubredditFromPost() {
        // Given
        post1.setSubreddits(new HashSet<>(Set.of(programmingSubreddit))); // Remove technologySubreddit

        // When
        Post updatedPost = postRepository.save(post1);

        // Then
        assertThat(updatedPost.getSubreddits()).hasSize(1);
        assertThat(updatedPost.getSubreddits()).contains(programmingSubreddit);
        assertThat(updatedPost.getSubreddits()).doesNotContain(technologySubreddit);
    }

    @Test
    @DisplayName("Should delete post and its subreddit relationships")
    void shouldDeletePostAndSubredditRelationships() {
        // Given
        Long postId = post1.getPostId();

        // When
        postRepository.delete(post1);

        // Then
        assertThat(postRepository.findById(postId)).isEmpty();
        
        // Verify subreddit relationships are also deleted
        List<Post> programmingPosts = postRepository.findBySubredditsContaining(programmingSubreddit);
        assertThat(programmingPosts).hasSize(1); // Only post3 remains
        assertThat(programmingPosts).noneMatch(post -> post.getPostId().equals(postId));
    }

    @Test
    @DisplayName("Should find all posts")
    void shouldFindAllPosts() {
        // When
        List<Post> allPosts = postRepository.findAll();

        // Then
        assertThat(allPosts).hasSize(3);
        assertThat(allPosts).extracting("postName")
                .containsExactlyInAnyOrder("Programming Post", "Gaming Post", "Tech Post");
    }

    @Test
    @DisplayName("Should find post by ID")
    void shouldFindPostById() {
        // When
        var foundPost = postRepository.findById(post1.getPostId());

        // Then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getPostName()).isEqualTo("Programming Post");
        assertThat(foundPost.get().getSubreddits()).hasSize(2);
    }
} 