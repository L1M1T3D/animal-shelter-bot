package ru.skypro.animalshelter.service;

import org.junit.jupiter.api.Test;
import ru.skypro.animalshelter.model.Adopter;
import ru.skypro.animalshelter.model.Adoption;
import ru.skypro.animalshelter.repository.AdoptionRepository;
import ru.skypro.animalshelter.repository.ReportRepository;
import ru.skypro.animalshelter.telegram.TelegramNotificationService;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Проверки автоматических напоминаний и уведомлений волонтёру. */
class ReportReminderServiceTest {
    private final AdoptionRepository adoptions = mock(AdoptionRepository.class);
    private final ReportRepository reports = mock(ReportRepository.class);
    private final TelegramNotificationService notifications = mock(TelegramNotificationService.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-08T08:00:00Z"), ZoneId.of("Asia/Almaty"));

    @Test
    void sendsReminderAndEscalationAfterTwoMissedDays() {
        Adoption adoption = mock(Adoption.class);
        Adopter adopter = mock(Adopter.class);
        when(adoption.getId()).thenReturn(7L);
        when(adoption.getAdopter()).thenReturn(adopter);
        when(adopter.getTelegramId()).thenReturn(100L);
        when(adoption.getStartedAt()).thenReturn(LocalDateTime.now(clock).minusDays(3));
        when(adoption.getEndsAt()).thenReturn(LocalDateTime.now(clock).plusDays(3));
        when(adoptions.findByStatusIn(AdoptionService.ACTIVE_STATUSES))
                .thenReturn(List.of(adoption));
        when(reports.findFirstByAdoptionIdOrderByCreatedAtDesc(7L)).thenReturn(Optional.empty());
        when(notifications.send(eq(-200L), anyString())).thenReturn(true);
        new ReportReminderService(adoptions, reports, notifications, clock, "-200")
                .sendDailyReminders();
        verify(notifications).send(eq(100L), contains("/report"));
        verify(notifications).send(eq(-200L), contains("№7"));
        verify(adoption).markEscalated(LocalDate.now(clock));
    }

    @Test
    void doesNothingWhenReportAlreadyReceivedToday() {
        Adoption adoption = mock(Adoption.class);
        when(adoption.getId()).thenReturn(7L);
        when(adoption.getStartedAt()).thenReturn(LocalDateTime.now(clock).minusDays(3));
        when(adoption.getEndsAt()).thenReturn(LocalDateTime.now(clock).plusDays(3));
        when(adoptions.findByStatusIn(AdoptionService.ACTIVE_STATUSES))
                .thenReturn(List.of(adoption));
        when(reports.existsByAdoptionIdAndReportDay(eq(7L), any(LocalDate.class)))
                .thenReturn(true);
        new ReportReminderService(adoptions, reports, notifications, clock, "-200")
                .sendDailyReminders();
        verifyNoInteractions(notifications);
    }
}
