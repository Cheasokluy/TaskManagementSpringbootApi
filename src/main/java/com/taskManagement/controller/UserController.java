package com.taskManagement.controller;

import com.taskManagement.entity.User;
import com.taskManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    // ==================== BASIC CRUD OPERATIONS ====================

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Creating user: {}", user.getUsername());
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Error creating user: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Getting user by ID: {}", id);
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        log.info("Getting user by username: {}", username);
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        log.info("Getting user by email: {}", email);
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Getting all users");
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getAllActiveUsers() {
        log.info("Getting all active users");
        List<User> users = userService.getAllActiveUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("Updating user with ID: {}", id);
        try {
            User updatedUser = userService.updateUser(id, user);
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
    public ResponseEntity<String> generatePasswordResetToken(@RequestBody PasswordResetRequest request) {
        log.info("Generating password reset token for email: {}", request.getEmail());
        try {
            userService.generatePasswordResetToken(request.getEmail());
            return new ResponseEntity<>("Password reset token generated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error generating password reset token: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetConfirm request) {
        log.info("Resetting password with token");
        try {
            boolean success = userService.resetPassword(request.getToken(), request.getNewPassword());
            if (success) {
                return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to reset password", HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            log.error("Error resetting password: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/change")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        log.info("Changing password for user ID: {}", request.getUserId());
        try {
            userService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
            return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error changing password: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== PROFILE MANAGEMENT ====================

    @PatchMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody ProfileUpdateRequest request) {
        log.info("Updating profile for user ID: {}", id);
        try {
            User updatedUser = userService.updateProfile(id, request.getFirstName(),
                    request.getLastName(), request.getPhoneNumber(), request.getJobTitle(), request.getBio());
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error updating profile: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/profile-picture")
    public ResponseEntity<User> updateProfilePicture(@PathVariable Long id, @RequestBody ProfilePictureRequest request) {
        log.info("Updating profile picture for user ID: {}", id);
        try {
            User updatedUser = userService.updateProfilePicture(id, request.getProfilePictureUrl());
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
    public ResponseEntity<String> verifyEmail(@PathVariable Long id, @RequestBody EmailVerificationRequest request) {
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

    // ==================== REQUEST/RESPONSE CLASSES ====================

    public static class PasswordResetRequest {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class PasswordResetConfirm {
        private String token;
        private String newPassword;
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class PasswordChangeRequest {
        private Long userId;
        private String oldPassword;
        private String newPassword;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class ProfileUpdateRequest {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String jobTitle;
        private String bio;
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
    }

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
