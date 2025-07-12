package com.programming.techie.springredditclone.exceptions;

public class InvalidPostContentException extends RuntimeException {
    public InvalidPostContentException(String message) {
        super(message);
    }
} 