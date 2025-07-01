package com.taskManagement.repository;

import com.taskManagement.entity.Notification;
import com.taskManagement.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    List<Notification> findByRecipientIdAndTypeOrderByCreatedAtDesc(Long recipientId, NotificationType type);

    long countByRecipientIdAndIsReadFalse(Long recipientId);

    List<Notification> findByExpiresAtBefore(LocalDateTime now);

    List<Notification> findByRecipientIdAndIsReadTrue(Long recipientId);

    List<Notification> findByTypeAndCreatedAtAfter(NotificationType type, LocalDateTime date);
}
