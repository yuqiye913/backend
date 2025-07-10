package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.CursorPageResponse;
import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.dto.PostResponse;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.mapper.PostMapper;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.Subreddit;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.SubredditRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.PostService;
import com.programming.techie.springredditclone.util.CursorUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final BlockService blockService;
    private final PostMapper postMapper;
    private final CursorUtil cursorUtil;

    @Override
    public void save(PostRequest postRequest) {
        // Handle multiple subreddits only
        Set<Subreddit> subreddits = validateAndGetSubreddits(postRequest);
        if (subreddits.isEmpty()) {
            throw new RuntimeException("At least one subreddit must be specified");
        }
        
        if (postRequest.getPostName().isEmpty()) {
            throw new RuntimeException("Post name cannot be empty");
        }

        // Create post with multiple subreddits
        Post post = new Post();
        post.setPostName(postRequest.getPostName());
        post.setDescription(postRequest.getDescription());
        post.setUrl(postRequest.getUrl());
        post.setUser(authService.getCurrentUser());
        post.setSubreddits(subreddits);
        post.setCreatedDate(Instant.now());
        post.setVoteCount(0);
        
        postRepository.save(post);
    }

    /**
     * Validate and retrieve subreddits by names
     */
    private Set<Subreddit> validateAndGetSubreddits(PostRequest postRequest) {
        Set<Subreddit> subreddits = new HashSet<>();
        
        // Validate that subreddit names are provided
        if (postRequest.getSubredditNames() == null || postRequest.getSubredditNames().isEmpty()) {
            throw new RuntimeException("At least one subreddit must be specified");
        }
        
        // Get all subreddits by names
        for (String subredditName : postRequest.getSubredditNames()) {
            Subreddit subreddit = subredditRepository.findByName(subredditName)
                    .orElseThrow(() -> new RuntimeException("Subreddit not found with name - " + subredditName));
            subreddits.add(subreddit);
        }
        
        return subreddits;
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id - " + id));
        return postMapper.mapToDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<PostResponse> getAllPosts(String cursor, int limit) {
        Instant createdDate = Instant.now();
        Long postId = Long.MAX_VALUE;
        
        if (cursor != null && !cursor.isEmpty()) {
            CursorUtil.CursorData cursorData = cursorUtil.decodeCursor(cursor);
            createdDate = cursorData.getCreatedDate();
            postId = cursorData.getId();
        }
        
        List<Post> posts = postRepository.findAllWithCursor(createdDate, postId, PageRequest.of(0, limit + 1));
        
        boolean hasMore = posts.size() > limit;
        if (hasMore) {
            posts = posts.subList(0, limit);
        }
        
        List<PostResponse> postResponses = posts.stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
        
        String nextCursor = null;
        if (hasMore && !posts.isEmpty()) {
            Post lastPost = posts.get(posts.size() - 1);
            nextCursor = cursorUtil.encodeCursor(lastPost.getCreatedDate(), lastPost.getPostId());
        }
        
        return new CursorPageResponse<>(postResponses, nextCursor, hasMore, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<PostResponse> getPostsBySubreddit(Long subredditId, String cursor, int limit) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new RuntimeException("Subreddit not found with id - " + subredditId));
        
        Instant createdDate = Instant.now();
        Long postId = Long.MAX_VALUE;
        
        if (cursor != null && !cursor.isEmpty()) {
            CursorUtil.CursorData cursorData = cursorUtil.decodeCursor(cursor);
            createdDate = cursorData.getCreatedDate();
            postId = cursorData.getId();
        }
        
        List<Post> posts = postRepository.findBySubredditWithCursor(subreddit, createdDate, postId, PageRequest.of(0, limit + 1));
        
        boolean hasMore = posts.size() > limit;
        if (hasMore) {
            posts = posts.subList(0, limit);
        }
        
        List<PostResponse> postResponses = posts.stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
        
        String nextCursor = null;
        if (hasMore && !posts.isEmpty()) {
            Post lastPost = posts.get(posts.size() - 1);
            nextCursor = cursorUtil.encodeCursor(lastPost.getCreatedDate(), lastPost.getPostId());
        }
        
        return new CursorPageResponse<>(postResponses, nextCursor, hasMore, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<PostResponse> getPostsByUsername(String username, String cursor, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username - " + username));
        
        User currentUser = authService.getCurrentUser();
        
        // Check if current user is blocked by target user or has blocked target user
        if (blockService.isBlockedByUser(user.getUserId()) || blockService.hasBlockedUser(user.getUserId())) {
            throw new SpringRedditException("Cannot view posts due to block restrictions");
        }
        
        Instant createdDate = Instant.now();
        Long postId = Long.MAX_VALUE;
        
        if (cursor != null && !cursor.isEmpty()) {
            CursorUtil.CursorData cursorData = cursorUtil.decodeCursor(cursor);
            createdDate = cursorData.getCreatedDate();
            postId = cursorData.getId();
        }
        
        List<Post> posts = postRepository.findByUserWithCursor(user, createdDate, postId, PageRequest.of(0, limit + 1));
        
        boolean hasMore = posts.size() > limit;
        if (hasMore) {
            posts = posts.subList(0, limit);
        }
        
        List<PostResponse> postResponses = posts.stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
        
        String nextCursor = null;
        if (hasMore && !posts.isEmpty()) {
            Post lastPost = posts.get(posts.size() - 1);
            nextCursor = cursorUtil.encodeCursor(lastPost.getCreatedDate(), lastPost.getPostId());
        }
        
        return new CursorPageResponse<>(postResponses, nextCursor, hasMore, limit);
    }

    /**
     * Get posts that belong to any of the specified subreddits
     */
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByMultipleSubreddits(List<String> subredditNames) {
        Set<Subreddit> subreddits = subredditNames.stream()
                .map(name -> subredditRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Subreddit not found with name - " + name)))
                .collect(Collectors.toSet());
        
        return postRepository.findBySubredditsIn(subreddits)
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updatePost(Long postId, PostRequest postRequest) {
        // Find the existing post
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id - " + postId));
        
        // Validate that the current user owns the post
        User currentUser = authService.getCurrentUser();
        if (!existingPost.getUser().equals(currentUser)) {
            throw new RuntimeException("You can only update your own posts");
        }
        
        // Validate subreddits
        Set<Subreddit> subreddits = validateAndGetSubreddits(postRequest);
        if (subreddits.isEmpty()) {
            throw new RuntimeException("At least one subreddit must be specified");
        }
        
        // Validate post name
        if (postRequest.getPostName() == null || postRequest.getPostName().trim().isEmpty()) {
            throw new RuntimeException("Post name cannot be empty");
        }
        
        // Update the post fields
        existingPost.setPostName(postRequest.getPostName().trim());
        existingPost.setDescription(postRequest.getDescription());
        existingPost.setUrl(postRequest.getUrl());
        existingPost.setSubreddits(subreddits);
        
        // Save the updated post
        postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long postId) {
        // Find the existing post
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id - " + postId));
        
        // Validate that the current user owns the post
        User currentUser = authService.getCurrentUser();
        if (!existingPost.getUser().equals(currentUser)) {
            throw new RuntimeException("You can only delete your own posts");
        }
        
        // Delete the post
        postRepository.delete(existingPost);
    }
} 