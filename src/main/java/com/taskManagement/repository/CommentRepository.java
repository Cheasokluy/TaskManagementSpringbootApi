package com.taskManagement.repository;
import com.taskManagement.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    List<Comment> findByAuthorId(Long authorId);

    List<Comment> findByParentCommentId(Long parentCommentId);

    List<Comment> findByTaskIdAndParentCommentIsNullOrderByCreatedAtAsc(Long taskId);

    long countByTaskId(Long taskId);

    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

}
