package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockListResponseDto {
    private List<BlockResponseDto> blockedUsers;
    private Long totalCount;
    private Long activeBlockCount;
    private boolean hasMore;
    private Long nextPage;
    private Long currentPage;
    private Long pageSize;
    
    // Block statistics
    private Long totalBlocksByUser;
    private Long totalBlocksOfUser;
    private Long mutualBlockCount;
    private String mostCommonBlockReason;
    
    // Block summary
    private String blockSummary; // Brief summary of blocking activity
} 