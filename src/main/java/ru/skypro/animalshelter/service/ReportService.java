package ru.skypro.animalshelter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.animalshelter.dto.ReportRequest;
import ru.skypro.animalshelter.dto.ReportResponse;
import ru.skypro.animalshelter.exception.*;
import ru.skypro.animalshelter.model.*;
import ru.skypro.animalshelter.repository.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Сервис ежедневных отчётов с сохранением промежуточных черновиков. */
@Service
@Transactional(readOnly = true)
public class ReportService {
    /** Результат обработки части ежедневного отчёта. */
    public enum Progress { NEED_TEXT, NEED_PHOTO, COMPLETE }

    private final ReportRepository reports;
    private final ReportDraftRepository drafts;
    private final AdoptionRepository adoptions;
    private final AdoptionService adoptionService;
    private final Clock clock;

    public ReportService(ReportRepository reports, ReportDraftRepository drafts,
                         AdoptionRepository adoptions, AdoptionService adoptionService, Clock clock) {
        this.reports = reports;
        this.drafts = drafts;
        this.adoptions = adoptions;
        this.adoptionService = adoptionService;
        this.clock = clock;
    }

    /** Подготавливает приём отчёта от Telegram-пользователя. */
    @Transactional
    public void start(long telegramId) {
        Adoption a = adoptionService.getActive(telegramId);
        ensureNotReportedToday(a);
        loadDraft(a);
    }

    /** Проверяет, начата ли загрузка отчёта. */
    public boolean hasDraft(long telegramId) {
        try {
            Adoption a = adoptionService.getActive(telegramId);
            return drafts.findByAdoptionId(a.getId()).isPresent();
        } catch (DomainNotFoundException | DomainConflictException exception) {
            return false;
        }
    }

    /** Сохраняет текст отчёта или завершает полный отчёт. */
    @Transactional
    public Progress addText(long telegramId, String text) {
        if (text == null || text.isBlank() || text.length() > 2000) {
            throw new BadReportException("Описание должно содержать от 1 до 2000 символов");
        }
        Adoption a = adoptionService.getActive(telegramId);
        ensureNotReportedToday(a);
        ReportDraft draft = requireDraft(a);
        draft.setDescription(text.strip(), LocalDateTime.now(clock));
        return finalizeIfComplete(a, draft);
    }

    /** Сохраняет Telegram file_id фото или завершает полный отчёт. */
    @Transactional
    public Progress addPhoto(long telegramId, String photoId, String caption) {
        if (photoId == null || photoId.isBlank() || photoId.length() > 300) {
            throw new BadReportException("Не удалось прочитать фотографию");
        }
        if (caption != null && caption.length() > 2000) {
            throw new BadReportException("Подпись слишком длинная");
        }
        Adoption a = adoptionService.getActive(telegramId);
        ensureNotReportedToday(a);
        ReportDraft draft = requireDraft(a);
        draft.setPhotoFileId(photoId, LocalDateTime.now(clock));
        if (caption != null && !caption.isBlank()) {
            draft.setDescription(caption.strip(), LocalDateTime.now(clock));
        }
        return finalizeIfComplete(a, draft);
    }

    /** Позволяет передать полный отчёт через REST API. */
    @Transactional
    public long create(ReportRequest request) {
        Adoption adoption = adoptions.findById(request.adoptionId())
                .orElseThrow(() -> new DomainNotFoundException("Усыновление не найдено"));
        if (!AdoptionService.ACTIVE_STATUSES.contains(adoption.getStatus())
                || LocalDateTime.now(clock).isAfter(adoption.getEndsAt())) {
            throw new DomainConflictException("Для этого усыновления отчёт недоступен");
        }
        if (request.description() == null || request.description().isBlank()
                || request.description().length() > 2000
                || request.photoFileId() == null || request.photoFileId().isBlank()
                || request.photoFileId().length() > 300) {
            throw new BadReportException("Требуется описание до 2000 символов и фото Telegram");
        }
        ensureNotReportedToday(adoption);
        Report saved = save(adoption, request.description().strip(), request.photoFileId());
        drafts.findByAdoptionId(adoption.getId()).ifPresent(drafts::delete);
        return saved.getId();
    }

    /** Возвращает отчёты усыновления, начиная с самого нового. */
    public List<ReportResponse> getByAdoptionId(long adoptionId) {
        if (!adoptions.existsById(adoptionId)) {
            throw new DomainNotFoundException("Усыновление не найдено");
        }
        return reports.findByAdoptionIdOrderByCreatedAtDesc(adoptionId)
                .stream().map(this::toResponse).toList();
    }

    /** Возвращает отдельный отчёт. */
    public ReportResponse getById(long id) {
        return toResponse(reports.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Отчёт не найден")));
    }

    private ReportDraft requireDraft(Adoption adoption) {
        return drafts.findByAdoptionId(adoption.getId())
                .orElseThrow(() -> new DomainConflictException("Сначала отправьте /report"));
    }

    private ReportDraft loadDraft(Adoption adoption) {
        ReportDraft draft = drafts.findByAdoptionId(adoption.getId())
                .orElseGet(() -> new ReportDraft(adoption, LocalDateTime.now(clock)));
        if (!draft.getUpdatedAt().toLocalDate().equals(LocalDate.now(clock))) {
            draft.reset(LocalDateTime.now(clock));
        }
        return drafts.save(draft);
    }

    private void ensureNotReportedToday(Adoption a) {
        if (reports.existsByAdoptionIdAndReportDay(a.getId(), LocalDate.now(clock))) {
            throw new DomainConflictException("Сегодняшний отчёт уже отправлен");
        }
    }

    private Progress finalizeIfComplete(Adoption adoption, ReportDraft draft) {
        if (!draft.isComplete()) {
            drafts.save(draft);
            return draft.getDescription() == null ? Progress.NEED_TEXT : Progress.NEED_PHOTO;
        }
        save(adoption, draft.getDescription(), draft.getPhotoFileId());
        drafts.delete(draft);
        return Progress.COMPLETE;
    }

    private Report save(Adoption adoption, String description, String photoFileId) {
        LocalDateTime now = LocalDateTime.now(clock);
        return reports.save(new Report(adoption, description, photoFileId,
                now, now.toLocalDate(), ReportStatus.PENDING));
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(report.getId(), report.getAdoption().getId(),
                report.getDescription(), report.getPhotoFileId(), report.getReportDay(),
                report.getStatus());
    }
}
