package com.taskManagement.repository;
import com.taskManagement.entity.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long>{
    List<FileAttachment> findByTaskId(Long taskId);

    List<FileAttachment> findByUploadedById(Long uploadedById);

    List<FileAttachment> findByTaskIdAndIsPublicTrue(Long taskId);

    List<FileAttachment> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    long countByTaskId(Long taskId);

    List<FileAttachment> findByFileTypeStartingWith(String fileTypePrefix);

    List<FileAttachment> findByUploadedByIdOrderByCreatedAtDesc(Long uploadedById);

}
