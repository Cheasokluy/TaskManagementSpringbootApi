package com.taskManagement.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;

    @Size(max = 100, message = "Job title cannot exceed 100 characters")
    private String jobTitle;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

}
