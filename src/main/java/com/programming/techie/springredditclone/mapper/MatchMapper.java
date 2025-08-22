package com.programming.techie.springredditclone.mapper;

import com.programming.techie.springredditclone.dto.MatchDto;
import com.programming.techie.springredditclone.model.Match;

public interface MatchMapper {
    
    /**
     * Map Match entity to MatchDto
     * @param match The Match entity
     * @return MatchDto
     */
    MatchDto mapToDto(Match match);
    
    /**
     * Map MatchDto to Match entity
     * @param matchDto The MatchDto
     * @return Match entity
     */
    Match mapToEntity(MatchDto matchDto);
} 