package com.eventforge.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventforge.notification.NotificationService;
import com.eventforge.notification.NotificationType;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeadlineNotificationSchedulerTest {
    @Test
    void emitsApproachingAndOverdueNotificationsForEligibleTasks() {
        EventTaskRepository tasks = mock(EventTaskRepository.class);
        NotificationService notifications = mock(NotificationService.class);
        EventTask approaching = mock(EventTask.class);
        EventTask overdue = mock(EventTask.class);
        when(approaching.getTitle()).thenReturn("Prepare venue");
        when(overdue.getTitle()).thenReturn("Pay deposit");
        Instant now = Instant.parse("2026-01-01T12:00:00Z");
        when(tasks.findApproachingDeadlines(eq(now), any(), eq(TaskStatus.COMPLETED)))
                .thenReturn(List.of(approaching));
        when(tasks.findOverdueDeadlines(now, TaskStatus.COMPLETED)).thenReturn(List.of(overdue));

        new DeadlineNotificationScheduler(tasks, notifications).processDeadlines(now);

        verify(notifications).recordTaskDeadline(eq(approaching), eq(NotificationType.DEADLINE),
                eq("Task deadline is approaching: Prepare venue"), eq(now));
        verify(notifications).recordTaskDeadline(eq(overdue), eq(NotificationType.OVERDUE),
                eq("Task is overdue: Pay deposit"), eq(now));
    }
}
