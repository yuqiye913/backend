package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotEmpty(message = "Email is required")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must be at most 50 characters")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(max = 100, message = "Password must be at most 100 characters")
    private String password;
}
