package com.taskManagement.dto.user;

import com.taskManagement.entity.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String profilePictureUrl;
    private UserRole role;
    private Boolean isActive;

}
