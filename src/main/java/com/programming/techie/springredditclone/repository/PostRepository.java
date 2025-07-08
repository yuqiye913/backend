package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.Subreddit;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findBySubredditsContaining(Subreddit subreddit);
    List<Post> findByUser(User user);
    
    // Find posts that belong to any of the specified subreddits
    List<Post> findBySubredditsIn(Set<Subreddit> subreddits);
}
