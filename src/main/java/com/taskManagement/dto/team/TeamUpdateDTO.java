package com.taskManagement.dto.team;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateDTO {
    @Size(min = 2, max = 100, message = "Team name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters")
    private String avatarUrl;

    @Size(min = 3, max = 20, message = "Team code must be between 3 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Team code must contain only uppercase letters and numbers")
    private String teamCode;

    @Min(value = 1, message = "Maximum members must be at least 1")
    @Max(value = 1000, message = "Maximum members cannot exceed 1000")
    private Integer maxMembers;

    private Boolean isActive;
}