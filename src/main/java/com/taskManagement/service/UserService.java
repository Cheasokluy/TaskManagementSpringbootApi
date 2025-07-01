package com.taskManagement.service;

import com.taskManagement.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // Basic CRUD operations
    User createUser(User user);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers();

    List<User> getAllActiveUsers();

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);

    // Authentication & validation methods
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Password management
    void generatePasswordResetToken(String email);

    boolean resetPassword(String token, String newPassword);

    void changePassword(Long userId, String oldPassword, String newPassword);

    // Profile management
    User updateProfile(Long id, String firstName, String lastName, String phoneNumber, String jobTitle, String bio);

    User updateProfilePicture(Long id, String profilePictureUrl);

    // Email verification
    void sendEmailVerification(Long userId);

    boolean verifyEmail(Long userId, String verificationToken);

}
