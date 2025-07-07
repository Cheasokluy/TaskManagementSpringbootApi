package com.taskManagement.mapper.task;

import com.taskManagement.entity.Task;
import com.taskManagement.entity.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Component
public class TaskMappingHelper {
    public static Double calculateCompletionPercentage(Set<Task> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) return 100.0;

        long completedCount = subtasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .count();

        return ((double) completedCount / subtasks.size()) * 100.0;
    }

    public static String calculateTimeRemaining(LocalDateTime dueDate) {
        if (dueDate == null) return "No due date";

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(dueDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, now);
            return daysOverdue + " days overdue";
        }

        long daysRemaining = ChronoUnit.DAYS.between(now, dueDate);
        if (daysRemaining == 0) {
            long hoursRemaining = ChronoUnit.HOURS.between(now, dueDate);
            return hoursRemaining + " hours remaining";
        }

        return daysRemaining + " days remaining";
    }

    public static String calculateTaskEfficiency(Task task) {
        if (task.getEstimatedHours() == null || task.getActualHours() == null) {
            return "Not calculated";
        }

        if (task.getEstimatedHours() == 0) return "No estimate";

        double efficiency = (task.getEstimatedHours() / task.getActualHours()) * 100;

        if (efficiency > 100) return "Ahead of schedule";
        if (efficiency < 100) return "Behind schedule";
        return "On schedule";
    }

}
