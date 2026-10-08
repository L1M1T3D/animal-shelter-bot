package ru.skypro.animalshelter.service;

import org.junit.jupiter.api.Test;
import ru.skypro.animalshelter.dto.ReportRequest;
import ru.skypro.animalshelter.exception.*;
import ru.skypro.animalshelter.model.*;
import ru.skypro.animalshelter.repository.*;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Проверки текста, фото и защиты от повторных ежедневных отчётов. */
class ReportServiceTest {
    private final ReportRepository reports = mock(ReportRepository.class);
    private final ReportDraftRepository drafts = mock(ReportDraftRepository.class);
    private final AdoptionRepository adoptions = mock(AdoptionRepository.class);
    private final AdoptionService adoptionService = mock(AdoptionService.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-08T08:00:00Z"), ZoneId.of("Asia/Almaty"));
    private final ReportService service = new ReportService(reports, drafts, adoptions, adoptionService, clock);

    @Test
    void completesTelegramReportWithTextThenPhoto() {
        Adoption adoption = mock(Adoption.class);
        when(adoption.getId()).thenReturn(7L);
        when(adoptionService.getActive(100L)).thenReturn(adoption);
        ReportDraft draft = new ReportDraft(adoption, LocalDateTime.now(clock));
        when(drafts.findByAdoptionId(7L)).thenReturn(Optional.of(draft));
        when(drafts.save(draft)).thenReturn(draft);
        assertThat(service.addText(100L, "Сегодня питомец хорошо поел"))
                .isEqualTo(ReportService.Progress.NEED_PHOTO);
        assertThat(service.addPhoto(100L, "telegram-photo-id", null))
                .isEqualTo(ReportService.Progress.COMPLETE);
        verify(reports).save(any(Report.class));
        verify(drafts).delete(draft);
    }

    @Test
    void completesPhotoFirstWithCaption() {
        Adoption adoption = mock(Adoption.class);
        when(adoption.getId()).thenReturn(7L);
        when(adoptionService.getActive(100L)).thenReturn(adoption);
        ReportDraft draft = new ReportDraft(adoption, LocalDateTime.now(clock));
        when(drafts.findByAdoptionId(7L)).thenReturn(Optional.of(draft));
        assertThat(service.addPhoto(100L, "photo-file", "Сегодня всё хорошо"))
                .isEqualTo(ReportService.Progress.COMPLETE);
    }

    @Test
    void refusesToAcceptTextWithoutReportCommand() {
        Adoption adoption = mock(Adoption.class);
        when(adoption.getId()).thenReturn(7L);
        when(adoptionService.getActive(100L)).thenReturn(adoption);
        when(drafts.findByAdoptionId(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.addText(100L, "описание"))
                .isInstanceOf(DomainConflictException.class);
    }

    @Test
    void refusesDuplicateDailyReport() {
        Adoption adoption = mock(Adoption.class);
        when(adoption.getId()).thenReturn(7L);
        when(adoptionService.getActive(100L)).thenReturn(adoption);
        when(reports.existsByAdoptionIdAndReportDay(eq(7L), any(LocalDate.class)))
                .thenReturn(true);
        assertThatThrownBy(() -> service.start(100L))
                .isInstanceOf(DomainConflictException.class);
    }

    @Test
    void refusesInvalidText() {
        assertThatThrownBy(() -> service.addText(100L, "  "))
                .isInstanceOf(BadReportException.class);
    }

    @Test
    void createsReportThroughRest() {
        Adoption adoption = mock(Adoption.class);
        when(adoption.getId()).thenReturn(7L);
        when(adoption.getStatus()).thenReturn(AdoptionStatus.TRIAL);
        when(adoption.getEndsAt()).thenReturn(LocalDateTime.now(clock).plusDays(10));
        when(adoptions.findById(7L)).thenReturn(Optional.of(adoption));
        Report saved = mock(Report.class);
        when(saved.getId()).thenReturn(18L);
        when(reports.save(any(Report.class))).thenReturn(saved);
        assertThat(service.create(new ReportRequest(7L, "Нормально ест", "file-id")))
                .isEqualTo(18L);
    }
}
