package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageResponse<T> {
    private List<T> content;
    private String nextCursor;
    private boolean hasMore;
    private int limit;
} 