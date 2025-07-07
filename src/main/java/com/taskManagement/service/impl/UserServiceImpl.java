package com.taskManagement.serviceImpl;

import com.taskManagement.dto.user.*;
import com.taskManagement.entity.User;
import com.taskManagement.mapper.UserMapper;

import com.taskManagement.repository.UserRepository;
import com.taskManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ==================== BASIC CRUD OPERATIONS ====================

    @Override
    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
        log.info("Creating user with username: {}", userCreateDTO.getUsername());

        // Validate unique constraints
        if (userRepository.existsByUsername(userCreateDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userCreateDTO.getUsername());
        }
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userCreateDTO.getEmail());
        }

        // Convert DTO to Entity
        User user = userMapper.toEntity(userCreateDTO);

        // Encode password
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));

        // Set default values
        user.setIsActive(true);
        user.setEmailVerified(false);
        user.setEmailVerificationToken(UUID.randomUUID().toString());

        // Save user
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(userMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(userMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        return userMapper.toResponseDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllActiveUsers() {
        log.debug("Fetching all active users");
        List<User> users = userRepository.findByIsActiveTrue();
        return userMapper.toResponseDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryDTO> getAllUsersSummary() {
        log.debug("Fetching all users summary");
        List<User> users = userRepository.findAll();
        return userMapper.toSummaryDTOList(users);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        // Check email uniqueness if email is being updated
        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userUpdateDTO.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + userUpdateDTO.getEmail());
            }
        }

        // Update fields
        userMapper.updateEntityFromDTO(user, userUpdateDTO);

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    @Override
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setIsActive(false);
        userRepository.save(user);

        log.info("User deactivated successfully with ID: {}", id);
    }

    @Override
    public void activateUser(Long id) {
        log.info("Activating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setIsActive(true);
        userRepository.save(user);

        log.info("User activated successfully with ID: {}", id);
    }

    // ==================== AUTHENTICATION & VALIDATION ====================

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ==================== PASSWORD MANAGEMENT ====================

    @Override
    public void generatePasswordResetToken(String email) {
        log.info("Generating password reset token for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(LocalDateTime.now().plusHours(1)); // 1 hour expiry

        userRepository.save(user);

        // TODO: Send email with reset token
        log.info("Password reset token generated for user: {}", user.getUsername());
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        log.info("Resetting password with token");

        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (user.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);

        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getUsername());
        return true;
    }

    @Override
    public void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO) {
        log.info("Changing password for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password confirmation
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    // ==================== PROFILE MANAGEMENT ====================

    @Override
    public UserResponseDTO updateProfile(Long id, UserProfileDTO profileDTO) {
        log.info("Updating profile for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        userMapper.updateProfileFromDTO(user, profileDTO);

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", user.getUsername());

        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public UserResponseDTO updateProfilePicture(Long id, String profilePictureUrl) {
        log.info("Updating profile picture for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setProfilePictureUrl(profilePictureUrl);

        User updatedUser = userRepository.save(user);
        log.info("Profile picture updated successfully for user: {}", user.getUsername());

        return userMapper.toResponseDTO(updatedUser);
    }

    // ==================== EMAIL VERIFICATION ====================

    @Override
    public void sendEmailVerification(Long userId) {
        log.info("Sending email verification for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (user.getEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }

        user.setEmailVerificationToken(UUID.randomUUID().toString());
        userRepository.save(user);

        // TODO: Send email with verification token
        log.info("Email verification token generated for user: {}", user.getUsername());
    }

    @Override
    public boolean verifyEmail(Long userId, String verificationToken) {
        log.info("Verifying email for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (!verificationToken.equals(user.getEmailVerificationToken())) {
            throw new IllegalArgumentException("Invalid email verification token");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);

        userRepository.save(user);

        log.info("Email verified successfully for user: {}", user.getUsername());
        return true;
    }

    // ==================== INTERNAL METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

}

