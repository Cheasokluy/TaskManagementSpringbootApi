package com.taskManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_dependencies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"prerequisite_task_id", "dependent_task_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor

public class TaskDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_task_id", nullable = false)
    private Task prerequisiteTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_task_id", nullable = false)
    private Task dependentTask;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DependencyType type = DependencyType.FINISH_TO_START;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
