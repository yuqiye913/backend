-- Sample test data for users
INSERT INTO users (user_id, username, password, email, created, enabled) VALUES
(1, 'testuser1', '$2a$10$testpasswordhash1', 'testuser1@example.com', '2024-01-01 10:00:00', true),
(2, 'testuser2', '$2a$10$testpasswordhash2', 'testuser2@example.com', '2024-01-01 11:00:00', true),
(3, 'testuser3', '$2a$10$testpasswordhash3', 'testuser3@example.com', '2024-01-01 12:00:00', false);

-- Sample test data for subreddits
INSERT INTO subreddit (id, name, description, created_date, user_id) VALUES
(1, 'testsubreddit1', 'Test subreddit 1 description', '2024-01-01 10:00:00', 1),
(2, 'testsubreddit2', 'Test subreddit 2 description', '2024-01-01 11:00:00', 2);

-- Sample test data for posts
INSERT INTO post (post_id, post_name, url, description, vote_count, user_id, subreddit_id, created_date) VALUES
(1, 'Test Post 1', 'http://example.com/1', 'Test post 1 description', 5, 1, 1, '2024-01-01 10:00:00'),
(2, 'Test Post 2', 'http://example.com/2', 'Test post 2 description', 3, 2, 1, '2024-01-01 11:00:00'),
(3, 'Test Post 3', 'http://example.com/3', 'Test post 3 description', 7, 1, 2, '2024-01-01 12:00:00'); 