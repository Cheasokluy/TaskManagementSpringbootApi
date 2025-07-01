package com.taskManagement.mapper;

import com.taskManagement.dto.user.*;
import com.taskManagement.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toEntity(UserCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Will be encoded in service
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setJobTitle(dto.getJobTitle());
        user.setBio(dto.getBio());
        user.setRole(dto.getRole());

        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .phoneNumber(user.getPhoneNumber())
                .jobTitle(user.getJobTitle())
                .bio(user.getBio())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .fullName(user.getFullName())
                .build();
    }

    public UserSummaryDTO toSummaryDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserSummaryDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }

    public void updateEntityFromDTO(User user, UserUpdateDTO dto) {
        if (dto == null || user == null) {
            return;
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getJobTitle() != null) {
            user.setJobTitle(dto.getJobTitle());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
    }

    public void updateProfileFromDTO(User user, UserProfileDTO dto) {
        if (dto == null || user == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getJobTitle() != null) {
            user.setJobTitle(dto.getJobTitle());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
    }

    public List<UserResponseDTO> toResponseDTOList(List<User> users) {
        return users.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UserSummaryDTO> toSummaryDTOList(List<User> users) {
        return users.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

}
