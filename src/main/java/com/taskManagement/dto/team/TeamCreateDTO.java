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
public class TeamCreateDTO {
    @NotBlank(message = "Team name is required")
    @Size(min = 2, max = 100, message = "Team name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Pattern(regexp = "^[A-Z0-9]{3,10}$", message = "Team code must be 3-10 uppercase letters and numbers")
    private String teamCode;

    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters")
    private String avatarUrl;

    @Min(value = 1, message = "Maximum members must be at least 1")
    @Max(value = 1000, message = "Maximum members cannot exceed 1000")
    private Integer maxMembers = 50; // Default value

    @Builder.Default
    private Boolean isActive = true;

}
