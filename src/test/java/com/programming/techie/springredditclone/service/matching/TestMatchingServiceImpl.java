package com.programming.techie.springredditclone.service.matching;

import com.programming.techie.springredditclone.dto.MatchDto;
import com.programming.techie.springredditclone.dto.MatchRequestDto;
import com.programming.techie.springredditclone.dto.MatchResponseDto;
import com.programming.techie.springredditclone.dto.MatchStatusDto;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.mapper.MatchMapper;
import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.MatchRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.impl.MatchingServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Test-specific implementation of MatchingServiceImpl that overrides getCurrentUser()
 * to return a test user instead of throwing an exception.
 */
public class TestMatchingServiceImpl extends MatchingServiceImpl {

    private final User testCurrentUser;

    public TestMatchingServiceImpl(MatchRepository matchRepository, 
                                 UserRepository userRepository, 
                                 BlockService blockService,
                                 MatchMapper matchMapper,
                                 User testCurrentUser) {
        super(matchRepository, userRepository, blockService, matchMapper);
        this.testCurrentUser = testCurrentUser;
    }

    @Override
    protected User getCurrentUser() {
        return testCurrentUser;
    }
} 