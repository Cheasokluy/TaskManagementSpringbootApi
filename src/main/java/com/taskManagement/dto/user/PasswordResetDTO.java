package com.taskManagement.dto.user;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

}
@Data
@NoArgsConstructor
@AllArgsConstructor
class PasswordResetConfirmDTO {

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}

