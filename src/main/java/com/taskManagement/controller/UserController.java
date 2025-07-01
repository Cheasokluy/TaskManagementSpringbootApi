package com.taskManagement.controller;

import com.taskManagement.dto.user.*;
import com.taskManagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        log.info("Creating user: {}", userCreateDTO.getUsername());
        try {
            UserResponseDTO createdUser = userService.createUser(userCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            log.error("Error creating user: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error creating user: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("Getting user by ID: {}", id);
        Optional<UserResponseDTO> user = userService.getUserById(id);
        return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        log.info("Getting user by username: {}", username);
        Optional<UserResponseDTO> user = userService.getUserByUsername(username);
        return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.info("Getting user by email: {}", email);
        Optional<UserResponseDTO> user = userService.getUserByEmail(email);
        return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("Getting all users");
        List<UserResponseDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponseDTO>> getAllActiveUsers() {
        log.info("Getting all active users");
        List<UserResponseDTO> users = userService.getAllActiveUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsersSummary() {
        log.info("Getting all users summary");
        List<UserSummaryDTO> users = userService.getAllUsersSummary();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                      @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("Updating user with ID: {}", id);
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, userUpdateDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error updating user: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            log.error("Error deleting user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        log.info("Deactivating user with ID: {}", id);
        try {
            userService.deactivateUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error deactivating user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        log.info("Activating user with ID: {}", id);
        try {
            userService.activateUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error activating user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ==================== AUTHENTICATION & VALIDATION ====================

    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
        log.info("Checking if username exists: {}", username);
        boolean exists = userService.existsByUsername(username);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        log.info("Checking if email exists: {}", email);
        boolean exists = userService.existsByEmail(email);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    // ==================== PASSWORD MANAGEMENT ====================

    @PostMapping("/password/reset-request")
    public ResponseEntity<String> generatePasswordResetToken(@Valid @RequestBody PasswordResetDTO request) {
        log.info("Generating password reset token for email: {}", request.getEmail());
        try {
            userService.generatePasswordResetToken(request.getEmail());
            return new ResponseEntity<>("Password reset token generated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error generating password reset token: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/password/change/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable Long userId,
                                                 @Valid @RequestBody PasswordChangeDTO request) {
        log.info("Changing password for user ID: {}", userId);
        try {
            userService.changePassword(userId, request);
            return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error changing password: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== PROFILE MANAGEMENT ====================

    @PatchMapping("/{id}/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(@PathVariable Long id,
                                                         @Valid @RequestBody UserProfileDTO profileDTO) {
        log.info("Updating profile for user ID: {}", id);
        try {
            UserResponseDTO updatedUser = userService.updateProfile(id, profileDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error updating profile: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/profile-picture")
    public ResponseEntity<UserResponseDTO> updateProfilePicture(@PathVariable Long id,
                                                                @RequestBody ProfilePictureRequest request) {
        log.info("Updating profile picture for user ID: {}", id);
        try {
            UserResponseDTO updatedUser = userService.updateProfilePicture(id, request.getProfilePictureUrl());
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error updating profile picture: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ==================== EMAIL VERIFICATION ====================

    @PostMapping("/{id}/email/verify/send")
    public ResponseEntity<String> sendEmailVerification(@PathVariable Long id) {
        log.info("Sending email verification for user ID: {}", id);
        try {
            userService.sendEmailVerification(id);
            return new ResponseEntity<>("Email verification sent successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error sending email verification: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/email/verify")
    public ResponseEntity<String> verifyEmail(@PathVariable Long id,
                                              @RequestBody EmailVerificationRequest request) {
        log.info("Verifying email for user ID: {}", id);
        try {
            boolean success = userService.verifyEmail(id, request.getVerificationToken());
            if (success) {
                return new ResponseEntity<>("Email verified successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to verify email", HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            log.error("Error verifying email: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
