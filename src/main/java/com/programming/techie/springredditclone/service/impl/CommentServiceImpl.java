package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.CommentsDto;
import com.programming.techie.springredditclone.dto.CursorPageResponse;
import com.programming.techie.springredditclone.exceptions.PostNotFoundException;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.mapper.CommentMapper;
import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.NotificationEmail;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.CommentRepository;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.event.PostCommentedEvent;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.CommentService;
import com.programming.techie.springredditclone.service.NotificationService;
import com.programming.techie.springredditclone.util.CursorUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final String POST_URL = "";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final BlockService blockService;
    private final CursorUtil cursorUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void save(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
        
        User currentUser = authService.getCurrentUser();
        User postOwner = post.getUser();
        
        // Check if current user is blocked by post owner or has blocked post owner
        if (blockService.isBlockedByUser(postOwner.getUserId()) || blockService.hasBlockedUser(postOwner.getUserId())) {
            throw new SpringRedditException("Cannot comment on this post due to block restrictions");
        }
        
        Comment comment = commentMapper.map(commentsDto, post, currentUser);
        commentRepository.save(comment);

        // Increment comment count for the post
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        // Publish comment event for notification
        eventPublisher.publishEvent(new PostCommentedEvent(this, comment.getUser(), post.getUser(), post, comment));
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new SpringRedditException("Comment not found with ID: " + commentId));
        
        // Check if user is authorized to delete this comment
        User currentUser = authService.getCurrentUser();
        if (!comment.getUser().equals(currentUser) && !comment.getPost().getUser().equals(currentUser)) {
            throw new SpringRedditException("You are not authorized to delete this comment");
        }
        
        // Soft delete the comment
        comment.setDeleted(true);
        commentRepository.save(comment);
        
        // Decrement comment count for the post
        Post post = comment.getPost();
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1)); // Ensure count doesn't go negative
        postRepository.save(post);
    }



    @Override
    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId.toString()));
        User currentUser = authService.getCurrentUser();
        
        return commentRepository.findByPost(post)
                .stream()
                .filter(comment -> {
                    // Filter out comments from blocked users or users who blocked current user
                    return !blockService.hasBlockedUser(comment.getUser().getUserId()) && 
                           !blockService.isBlockedByUser(comment.getUser().getUserId());
                })
                .map(commentMapper::mapToDto).toList();
    }

    @Override
    public CursorPageResponse<CommentsDto> getCommentsForPost(Long postId, String cursor, Integer limit) {
        // Validate post exists
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId.toString()));
        
        // Set default limit if not provided
        int actualLimit = (limit != null) ? Math.min(limit, 50) : 20; // Max 50, default 20
        
        List<Comment> comments;
        String nextCursor = null;
        
        if (cursor == null) {
            // First page
            comments = commentRepository.findTopLevelCommentsByPostFirstPage(postId);
        } else {
            // Subsequent pages
            CursorUtil.CursorData cursorData = cursorUtil.decodeCursor(cursor);
            comments = commentRepository.findTopLevelCommentsByPostWithCursor(postId, cursorData.getId());
        }
        
        // Apply limit and get next cursor
        if (comments.size() > actualLimit) {
            comments = comments.subList(0, actualLimit);
            Comment lastComment = comments.get(actualLimit - 1);
            nextCursor = cursorUtil.encodeCursor(lastComment.getCreatedDate(), lastComment.getId());
        }
        
        List<CommentsDto> commentDtos = comments.stream()
                .map(commentMapper::mapToDto)
                .toList();
        
        return new CursorPageResponse<>(commentDtos, nextCursor, nextCursor != null, actualLimit);
    }

    @Override
    public CursorPageResponse<CommentsDto> getCommentsForUser(String userName, String cursor, Integer limit) {
        // Validate user exists
        userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        
        // Set default limit if not provided
        int actualLimit = (limit != null) ? Math.min(limit, 50) : 20; // Max 50, default 20
        
        List<Comment> comments;
        String nextCursor = null;
        
        if (cursor == null) {
            // First page
            comments = commentRepository.findCommentsByUserFirstPage(userName);
        } else {
            // Subsequent pages
            CursorUtil.CursorData cursorData = cursorUtil.decodeCursor(cursor);
            comments = commentRepository.findCommentsByUserWithCursor(userName, cursorData.getId());
        }
        
        // Apply limit and get next cursor
        if (comments.size() > actualLimit) {
            comments = comments.subList(0, actualLimit);
            Comment lastComment = comments.get(actualLimit - 1);
            nextCursor = cursorUtil.encodeCursor(lastComment.getCreatedDate(), lastComment.getId());
        }
        
        List<CommentsDto> commentDtos = comments.stream()
                .map(commentMapper::mapToDto)
                .toList();
        
        return new CursorPageResponse<>(commentDtos, nextCursor, nextCursor != null, actualLimit);
    }

    @Override
    public CursorPageResponse<CommentsDto> getRepliesForComment(Long commentId, String cursor, Integer limit) {
        // Validate comment exists
        commentRepository.findById(commentId)
                .orElseThrow(() -> new SpringRedditException("Comment not found with ID: " + commentId));
        
        // Set default limit if not provided
        int actualLimit = (limit != null) ? Math.min(limit, 50) : 20; // Max 50, default 20
        
        List<Comment> replies;
        String nextCursor = null;
        
        if (cursor == null) {
            // First page
            replies = commentRepository.findRepliesByCommentFirstPage(commentId);
        } else {
            // Subsequent pages
            CursorUtil.CursorData cursorData = cursorUtil.decodeCursor(cursor);
            replies = commentRepository.findRepliesByCommentWithCursor(commentId, cursorData.getId());
        }
        
        // Apply limit and get next cursor
        if (replies.size() > actualLimit) {
            replies = replies.subList(0, actualLimit);
            Comment lastReply = replies.get(actualLimit - 1);
            nextCursor = cursorUtil.encodeCursor(lastReply.getCreatedDate(), lastReply.getId());
        }
        
        List<CommentsDto> replyDtos = replies.stream()
                .map(commentMapper::mapToDto)
                .toList();
        
        return new CursorPageResponse<>(replyDtos, nextCursor, nextCursor != null, actualLimit);
    }

    @Override
    public List<CommentsDto> getAllCommentsForUser(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .toList();
    }

    @Override
    public boolean containsSwearWords(String comment) {
        if (comment.contains("shit")) {
            throw new SpringRedditException("Comments contains unacceptable language");
        }
        return false;
    }
} 