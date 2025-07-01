package com.taskManagement.service;

import com.taskManagement.dto.user.*;
import com.taskManagement.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserService {
    // Basic CRUD operations with DTOs
    UserResponseDTO createUser(UserCreateDTO userCreateDTO);
    Optional<UserResponseDTO> getUserById(Long id);
    Optional<UserResponseDTO> getUserByUsername(String username);
    Optional<UserResponseDTO> getUserByEmail(String email);
    List<UserResponseDTO> getAllUsers();
    List<UserResponseDTO> getAllActiveUsers();
    List<UserSummaryDTO> getAllUsersSummary();

    UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);
    void deleteUser(Long id);
    void deactivateUser(Long id);
    void activateUser(Long id);

    // Authentication & validation
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Password management
    void generatePasswordResetToken(String email);
    boolean resetPassword(String token, String newPassword);
    void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO);

    // Profile management
    UserResponseDTO updateProfile(Long id, UserProfileDTO profileDTO);
    UserResponseDTO updateProfilePicture(Long id, String profilePictureUrl);

    // Email verification
    void sendEmailVerification(Long userId);
    boolean verifyEmail(Long userId, String verificationToken);

    // Internal methods (Entity-based for other services)
    User findUserEntityById(Long id);
    User findUserEntityByUsername(String username);


}
