package com.taskManagement.controller;

import com.taskManagement.dto.common.ApiResponse;
import com.taskManagement.dto.user.*;
import com.taskManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Validated
public class UserController {
    
    private final UserService userService;

    // ==================== BASIC CRUD OPERATIONS ====================

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        log.info("Creating user: {}", userCreateDTO.getUsername());
        try {
            UserResponseDTO createdUser = userService.createUser(userCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdUser, "User created successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create user: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        log.info("Getting user by ID: {}", id);
        try {
            Optional<UserResponseDTO> user = userService.getUserById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(user.get(), "User found"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found with ID: " + id));
            }
        } catch (Exception e) {
            log.error("Error getting user by ID: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get user: " + e.getMessage()));
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUsername(@PathVariable String username) {
        log.info("Getting user by username: {}", username);
        try {
            Optional<UserResponseDTO> user = userService.getUserByUsername(username);
            if (user.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(user.get(), "User found"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found with username: " + username));
            }
        } catch (Exception e) {
            log.error("Error getting user by username: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get user: " + e.getMessage()));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByEmail(@PathVariable String email) {
        log.info("Getting user by email: {}", email);
        try {
            Optional<UserResponseDTO> user = userService.getUserByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(user.get(), "User found"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found with email: " + email));
            }
        } catch (Exception e) {
            log.error("Error getting user by email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get user: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        log.info("Getting all users");
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(users, users.size()));
        } catch (Exception e) {
            log.error("Error getting all users: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get users: " + e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllActiveUsers() {
        log.info("Getting all active users");
        try {
            List<UserResponseDTO> users = userService.getAllActiveUsers();
            return ResponseEntity.ok(ApiResponse.success(users, users.size()));
        } catch (Exception e) {
            log.error("Error getting active users: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get active users: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<UserSummaryDTO>>> getAllUsersSummary() {
        log.info("Getting all users summary");
        try {
            List<UserSummaryDTO> users = userService.getAllUsersSummary();
            return ResponseEntity.ok(ApiResponse.success(users, users.size()));
        } catch (Exception e) {
            log.error("Error getting users summary: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get users summary: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Long id,
                                                                   @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("Updating user with ID: {}", id);
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, userUpdateDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted", "User deleted successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error deleting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deleting user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {
        log.info("Deactivating user with ID: {}", id);
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deactivated", "User deactivated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error deactivating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deactivating user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to deactivate user: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<String>> activateUser(@PathVariable Long id) {
        log.info("Activating user with ID: {}", id);
        try {
            userService.activateUser(id);
            return ResponseEntity.ok(ApiResponse.success("User activated", "User activated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error activating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error activating user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to activate user: " + e.getMessage()));
        }
    }

    // ==================== AUTHENTICATION & VALIDATION ====================

    @GetMapping("/exists/username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> existsByUsername(@PathVariable String username) {
        log.info("Checking if username exists: {}", username);
        try {
            boolean exists = userService.existsByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(exists, "Username check completed"));
        } catch (Exception e) {
            log.error("Error checking username existence: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check username: " + e.getMessage()));
        }
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> existsByEmail(@PathVariable String email) {
        log.info("Checking if email exists: {}", email);
        try {
            boolean exists = userService.existsByEmail(email);
            return ResponseEntity.ok(ApiResponse.success(exists, "Email check completed"));
        } catch (Exception e) {
            log.error("Error checking email existence: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check email: " + e.getMessage()));
        }
    }

    // ==================== PASSWORD MANAGEMENT ====================

    @PostMapping("/password/reset-request")
    public ResponseEntity<ApiResponse<String>> generatePasswordResetToken(@Valid @RequestBody PasswordResetDTO request) {
        log.info("Generating password reset token for: {}", request.getEmail());
        try {
            userService.generatePasswordResetToken(request.getEmail()); // This method returns void
            return ResponseEntity.ok(ApiResponse.success("Token generated", "Password reset token sent successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error generating password reset token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error generating password reset token: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to generate password reset token: " + e.getMessage()));
        }
    }

    @PostMapping("/password/change/{userId}")
    public ResponseEntity<ApiResponse<String>> changePassword(@PathVariable Long userId,
                                                              @Valid @RequestBody PasswordChangeDTO request) {
        log.info("Changing password for user ID: {}", userId);
        try {
            userService.changePassword(userId, request); // This method returns void
            return ResponseEntity.ok(ApiResponse.success("Password changed", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error changing password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error changing password: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to change password: " + e.getMessage()));
        }
    }

    // ==================== PROFILE MANAGEMENT ====================

    @PatchMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(@PathVariable Long id,
                                                                      @Valid @RequestBody UserProfileDTO profileDTO) {
        log.info("Updating profile for user ID: {}", id);
        try {
            UserResponseDTO updatedUser = userService.updateProfile(id, profileDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "Profile updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating profile: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/profile-picture")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfilePicture(@PathVariable Long id,
                                                                             @RequestBody ProfilePictureRequest request) {
        log.info("Updating profile picture for user ID: {}", id);
        try {
            UserResponseDTO updatedUser = userService.updateProfilePicture(id, request.getProfilePictureUrl());
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "Profile picture updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating profile picture: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating profile picture: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update profile picture: " + e.getMessage()));
        }
    }

    // ==================== EMAIL VERIFICATION ====================

    @PostMapping("/{id}/email/verify/send")
    public ResponseEntity<ApiResponse<String>> sendEmailVerification(@PathVariable Long id) {
        log.info("Sending email verification for user ID: {}", id);
        try {
            userService.sendEmailVerification(id); // This method returns void
            return ResponseEntity.ok(ApiResponse.success("Verification sent", "Email verification sent successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error sending email verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error sending email verification: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send email verification: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/email/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@PathVariable Long id,
                                                           @RequestBody EmailVerificationRequest request) {
        log.info("Verifying email for user ID: {}", id);
        try {
            boolean verified = userService.verifyEmail(id, request.getVerificationToken()); // This method returns boolean
            if (verified) {
                return ResponseEntity.ok(ApiResponse.success("Email verified", "Email verified successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid verification token"));
            }
        } catch (IllegalArgumentException e) {
            log.error("Error verifying email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error verifying email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to verify email: " + e.getMessage()));
        }
    }

    // ==================== REQUEST CLASSES ====================

    public static class ProfilePictureRequest {
        private String profilePictureUrl;
        public String getProfilePictureUrl() { return profilePictureUrl; }
        public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    }

    public static class EmailVerificationRequest {
        private String verificationToken;
        public String getVerificationToken() { return verificationToken; }
        public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
    }
}