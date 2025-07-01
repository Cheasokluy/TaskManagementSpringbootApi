package com.taskManagement.service;
import com.taskManagement.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    // Basic CRUD operations
    Comment createComment(Comment comment);

    Optional<Comment> getCommentById(Long id);

    Comment updateComment(Long id, String content);

    void deleteComment(Long id);

    // Comments by task
    List<Comment> getCommentsByTaskId(Long taskId);

    List<Comment> getCommentsByTaskIdOrderByDate(Long taskId);

    List<Comment> getCommentsByTaskIdOrderByDateDesc(Long taskId);

    List<Comment> getTopLevelCommentsByTaskId(Long taskId);

    // Comments by user
    List<Comment> getCommentsByAuthorId(Long authorId);

    List<Comment> getCommentsByAuthorIdOrderByDate(Long authorId);

    // Reply management
    Comment replyToComment(Long parentCommentId, Comment reply);

    List<Comment> getRepliesForComment(Long commentId);

    // Comment statistics
    long countCommentsByTaskId(Long taskId);

    long countRepliesForComment(Long commentId);

    // Comment validation
    boolean canUserEditComment(Long userId, Long commentId);

    boolean canUserDeleteComment(Long userId, Long commentId);

    boolean canUserCommentOnTask(Long userId, Long taskId);

    // Comment moderation
    Comment markAsEdited(Long commentId);

    List<Comment> getRecentCommentsByUser(Long userId, int limit);

}
