package com.eventforge.task;

import com.eventforge.notification.NotificationService;
import com.eventforge.notification.NotificationType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Emits at most one approaching or overdue deadline notification for a task per UTC day. */
@Component
@EnableScheduling
public class DeadlineNotificationScheduler {
    private static final long APPROACHING_WINDOW_HOURS = 24;
    private final EventTaskRepository tasks;
    private final NotificationService notifications;

    public DeadlineNotificationScheduler(EventTaskRepository tasks, NotificationService notifications) {
        this.tasks = tasks;
        this.notifications = notifications;
    }

    @Scheduled(cron = "${eventforge.deadline-notifications.cron:0 0 * * * *}")
    @Transactional
    public void processDeadlines() {
        processDeadlines(Instant.now());
    }

    void processDeadlines(Instant now) {
        Instant approachingBy = now.plus(APPROACHING_WINDOW_HOURS, ChronoUnit.HOURS);
        tasks.findApproachingDeadlines(now, approachingBy, TaskStatus.COMPLETED).forEach(task ->
                notifications.recordTaskDeadline(task, NotificationType.DEADLINE,
                        "Task deadline is approaching: " + task.getTitle(), now));
        tasks.findOverdueDeadlines(now, TaskStatus.COMPLETED).forEach(task ->
                notifications.recordTaskDeadline(task, NotificationType.OVERDUE,
                        "Task is overdue: " + task.getTitle(), now));
    }
}
