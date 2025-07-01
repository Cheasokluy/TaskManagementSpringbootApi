package com.taskManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class FileAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileType; // MIME type

    @Column(nullable = false)
    private Long fileSize; // in bytes

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "download_count")
    private Long downloadCount = 0L;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @Column(name = "checksum")
    private String checksum; // For file integrity

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Extract file extension from original filename
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public boolean isImage() {
        return fileType != null && fileType.startsWith("image/");
    }

    public boolean isDocument() {
        return fileType != null && (
                fileType.equals("application/pdf") ||
                        fileType.equals("application/msword") ||
                        fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        fileType.equals("application/vnd.ms-excel") ||
                        fileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        );
    }

}
