package com.taskManagement.service;
import com.taskManagement.entity.FileAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FileAttachmentService {
    // Basic CRUD operations
    FileAttachment uploadFile(Long taskId, Long userId, MultipartFile file);

    Optional<FileAttachment> getFileById(Long id);

    void deleteFile(Long id);

    // File retrieval
    List<FileAttachment> getFilesByTaskId(Long taskId);

    List<FileAttachment> getFilesByTaskIdOrderByDate(Long taskId);

    List<FileAttachment> getFilesByUploaderId(Long uploaderId);

    List<FileAttachment> getFilesByUploaderIdOrderByDate(Long uploaderId);

    List<FileAttachment> getPublicFilesByTaskId(Long taskId);

    // File type filtering
    List<FileAttachment> getImagesByTaskId(Long taskId);

    List<FileAttachment> getDocumentsByTaskId(Long taskId);

    List<FileAttachment> getFilesByType(String fileType);

    // File statistics
    long countFilesByTaskId(Long taskId);

    Long getTotalFileSizeByTaskId(Long taskId);

    Long getTotalFileSizeByUploader(Long uploaderId);

    // File management
    FileAttachment updateFileVisibility(Long fileId, boolean isPublic);

    FileAttachment incrementDownloadCount(Long fileId);

    // File validation
    boolean canUserAccessFile(Long userId, Long fileId);

    boolean canUserDeleteFile(Long userId, Long fileId);

    boolean isValidFileType(String fileType);

    boolean isFileSizeAllowed(long fileSize);

    // File operations
    byte[] downloadFile(Long fileId);

    String generateDownloadUrl(Long fileId);

    void cleanupOrphanedFiles();

}
