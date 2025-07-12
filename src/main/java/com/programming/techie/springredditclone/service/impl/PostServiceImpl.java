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
import com.programming.techie.springredditclone.repository.VoteRepository;
import com.programming.techie.springredditclone.model.Vote;
import com.programming.techie.springredditclone.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import com.programming.techie.springredditclone.exceptions.InvalidPostContentException;

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
    private final VoteRepository voteRepository;

    @Override
    public void save(PostRequest postRequest) {
        // Handle multiple subreddits only
        Set<Subreddit> subreddits = validateAndGetSubreddits(postRequest);
        if (subreddits.isEmpty()) {
            throw new RuntimeException("At least one subreddit must be specified");
        }
        
        if (postRequest.getPostName() == null || postRequest.getPostName().trim().isEmpty()) {
            throw new RuntimeException("Post name cannot be empty");
        }
        
        // Enhanced content validation that checks for meaningful content beyond hashtags
        validatePostContent(postRequest.getDescription());

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
     * Validates that post content has meaningful text beyond just hashtags
     */
    private void validatePostContent(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidPostContentException("Post content/description cannot be empty");
        }
        
        // Remove hashtags and check if there's meaningful content left
        String contentWithoutHashtags = description.replaceAll("#\\w+", "").trim();
        if (contentWithoutHashtags.isEmpty()) {
            throw new InvalidPostContentException("Post content cannot consist only of hashtags");
        }
        
        // Check if remaining content has at least one alphanumeric character
        if (!contentWithoutHashtags.matches(".*[a-zA-Z0-9].*")) {
            throw new InvalidPostContentException("Post content must contain meaningful text beyond hashtags");
        }
    }

    /**
     * Validate and retrieve subreddits by names, creating new ones if they don't exist
     */
    private Set<Subreddit> validateAndGetSubreddits(PostRequest postRequest) {
        Set<Subreddit> subreddits = new HashSet<>();
        
        // Validate that subreddit names are provided
        if (postRequest.getSubredditNames() == null || postRequest.getSubredditNames().isEmpty()) {
            throw new RuntimeException("At least one subreddit must be specified");
        }
        
        User currentUser = authService.getCurrentUser();
        
        // Get or create subreddits by names
        for (String subredditName : postRequest.getSubredditNames()) {
            if (subredditName == null || subredditName.trim().isEmpty()) {
                throw new RuntimeException("Subreddit name cannot be empty");
            }
            
            String trimmedName = subredditName.trim();
            Subreddit subreddit = subredditRepository.findByName(trimmedName)
                    .orElseGet(() -> {
                        // Create new subreddit if it doesn't exist
                        Subreddit newSubreddit = Subreddit.builder()
                                .name(trimmedName)
                                .description("Created by " + currentUser.getUsername())
                                .user(currentUser)
                                .createdDate(Instant.now())
                                .build();
                        return subredditRepository.save(newSubreddit);
                    });
            subreddits.add(subreddit);
        }
        
        return subreddits;
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id - " + id));
        
        PostResponse response = postMapper.mapToDto(post);
        
        // Add vote status if user is authenticated
        if (authService.isLoggedIn()) {
            User currentUser = authService.getCurrentUser();
            response.setUpVote(isPostUpVoted(post, currentUser));
            response.setDownVote(isPostDownVoted(post, currentUser));
        }
        
        return response;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Jwt) {
            User currentUser = authService.getCurrentUser();
            if (blockService.isBlockedByUser(user.getUserId()) || blockService.hasBlockedUser(user.getUserId())) {
                throw new SpringRedditException("Cannot view posts due to block restrictions");
            }
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
                        .orElse(null)) // Return null if subreddit doesn't exist
                .filter(subreddit -> subreddit != null) // Filter out non-existent subreddits
                .collect(Collectors.toSet());
        
        // If no valid subreddits found, return empty list
        if (subreddits.isEmpty()) {
            return new ArrayList<>();
        }
        
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
        
        // Enhanced content validation that checks for meaningful content beyond hashtags
        validatePostContent(postRequest.getDescription());
        
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
    
    private boolean isPostUpVoted(Post post, User user) {
        return checkVoteType(post, user, VoteType.UPVOTE);
    }
    
    private boolean isPostDownVoted(Post post, User user) {
        return checkVoteType(post, user, VoteType.DOWNVOTE);
    }
    
    private boolean checkVoteType(Post post, User user, VoteType voteType) {
        return voteRepository.findByPostAndUser(post, user)
                .map(vote -> vote.getVoteType().equals(voteType))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> searchPostsBySubreddit(String subredditName) {
        List<Post> posts = postRepository.findBySubredditName(subredditName);
        return posts.stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> searchPosts(String searchTerm) {
        List<Post> posts = postRepository.searchPosts(searchTerm);
        return posts.stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPromotedPosts(int limit) {
        // TODO: Implement promotion algorithm
        // This method should return posts that are promoted based on various factors such as:
        // - High engagement (likes, comments, shares)
        // - Recent activity
        // - User reputation/credibility
        // - Content quality metrics
        // - Trending topics
        // - Sponsored content (if applicable)
        
        log.info("Getting promoted posts with limit: {}", limit);
        
        // Placeholder implementation - currently returns recent posts with high vote counts
        // This should be replaced with a proper promotion algorithm
        List<Post> promotedPosts = postRepository.findTopPostsByVoteCount(PageRequest.of(0, limit));
        
        return promotedPosts.stream()
                .map(post -> {
                    PostResponse response = postMapper.mapToDto(post);
                    // Add vote status if user is authenticated
                    if (authService.isLoggedIn()) {
                        User currentUser = authService.getCurrentUser();
                        response.setUpVote(isPostUpVoted(post, currentUser));
                        response.setDownVote(isPostDownVoted(post, currentUser));
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<PostResponse> getPromotedPosts(String cursor, int limit) {
        // TODO: Implement promotion algorithm with pagination
        // This method should return posts that are promoted based on various factors such as:
        // - High engagement (likes, comments, shares)
        // - Recent activity
        // - User reputation/credibility
        // - Content quality metrics
        // - Trending topics
        // - Sponsored content (if applicable)
        
        log.info("Getting promoted posts with cursor: {} and limit: {}", cursor, limit);
        
        // Initialize cursor values for first page
        Integer voteCount = Integer.MAX_VALUE;
        Instant createdDate = Instant.now();
        Long postId = Long.MAX_VALUE;
        
        if (cursor != null && !cursor.isEmpty()) {
            CursorUtil.CursorData cursorData = cursorUtil.decodeCursor(cursor);
            // For promoted posts, we need to decode vote count from cursor
            // This is a simplified approach - in a real implementation, you might want to encode vote count in the cursor
            createdDate = cursorData.getCreatedDate();
            postId = cursorData.getId();
            // Note: voteCount would need to be encoded in cursor for proper pagination
            // For now, we'll use a simplified approach
        }
        
        List<Post> posts = postRepository.findPromotedPostsWithCursor(voteCount, createdDate, postId, PageRequest.of(0, limit + 1));
        
        boolean hasMore = posts.size() > limit;
        if (hasMore) {
            posts = posts.subList(0, limit);
        }
        
        List<PostResponse> postResponses = posts.stream()
                .map(post -> {
                    PostResponse response = postMapper.mapToDto(post);
                    // Add vote status if user is authenticated
                    if (authService.isLoggedIn()) {
                        User currentUser = authService.getCurrentUser();
                        response.setUpVote(isPostUpVoted(post, currentUser));
                        response.setDownVote(isPostDownVoted(post, currentUser));
                    }
                    return response;
                })
                .collect(Collectors.toList());
        
        String nextCursor = null;
        if (hasMore && !posts.isEmpty()) {
            Post lastPost = posts.get(posts.size() - 1);
            nextCursor = cursorUtil.encodeCursor(lastPost.getCreatedDate(), lastPost.getPostId());
        }
        
        return new CursorPageResponse<>(postResponses, nextCursor, hasMore, limit);
    }
} 