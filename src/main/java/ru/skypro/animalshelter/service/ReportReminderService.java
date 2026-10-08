package ru.skypro.animalshelter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.animalshelter.model.Adoption;
import ru.skypro.animalshelter.repository.AdoptionRepository;
import ru.skypro.animalshelter.repository.ReportRepository;
import ru.skypro.animalshelter.telegram.TelegramNotificationService;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Ежедневные напоминания владельцам и уведомления волонтёру при пропусках. */
@Service
public class ReportReminderService {
    private static final long DAYS_BEFORE_ESCALATION = 2;
    private final AdoptionRepository adoptions;
    private final ReportRepository reports;
    private final TelegramNotificationService notifications;
    private final Clock clock;
    private final String volunteerChatId;

    public ReportReminderService(AdoptionRepository adoptions, ReportRepository reports,
            TelegramNotificationService notifications, Clock clock,
            @Value("${telegram.volunteer-chat-id:}") String volunteerChatId) {
        this.adoptions = adoptions;
        this.reports = reports;
        this.notifications = notifications;
        this.clock = clock;
        this.volunteerChatId = volunteerChatId;
    }

    /** Вызывается планировщиком ежедневно в часовой зоне приюта. */
    @Transactional
    @Scheduled(cron = "${app.report-reminder-cron:0 0 10 * * *}",
            zone = "${app.time-zone:Asia/Almaty}")
    public void sendDailyReminders() {
        LocalDate today = LocalDate.now(clock);
        for (Adoption adoption : adoptions.findByStatusIn(AdoptionService.ACTIVE_STATUSES)) {
            if (today.isBefore(adoption.getStartedAt().toLocalDate())
                    || today.isAfter(adoption.getEndsAt().toLocalDate())) {
                continue;
            }
            if (reports.existsByAdoptionIdAndReportDay(adoption.getId(), today)) {
                continue;
            }
            notifications.send(adoption.getAdopter().getTelegramId(),
                    "Не забудьте отправить ежедневный отчёт о питомце: /report");
            alertVolunteerIfNeeded(adoption, today);
        }
    }

    private void alertVolunteerIfNeeded(Adoption adoption, LocalDate today) {
        LocalDate lastReportDay = reports.findFirstByAdoptionIdOrderByCreatedAtDesc(adoption.getId())
                .map(report -> report.getReportDay())
                .orElse(adoption.getStartedAt().toLocalDate());
        if (ChronoUnit.DAYS.between(lastReportDay, today) < DAYS_BEFORE_ESCALATION
                || today.equals(adoption.getLastEscalationOn())
                || volunteerChatId.isBlank()) {
            return;
        }
        try {
            long volunteerId = Long.parseLong(volunteerChatId);
            boolean sent = notifications.send(volunteerId,
                    "Отчёты отсутствуют 2+ дня: усыновление №" + adoption.getId()
                            + ", Telegram ID владельца: " + adoption.getAdopter().getTelegramId());
            if (sent) {
                adoption.markEscalated(today);
                adoptions.save(adoption);
            }
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("TELEGRAM_VOLUNTEER_CHAT_ID должен быть числом", exception);
        }
    }
}
