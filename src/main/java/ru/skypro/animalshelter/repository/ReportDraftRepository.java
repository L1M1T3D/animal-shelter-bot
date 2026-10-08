package ru.skypro.animalshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.animalshelter.model.ReportDraft;
import java.util.Optional;

/** Хранение неполных отчётов до получения текста и фото. */
public interface ReportDraftRepository extends JpaRepository<ReportDraft, Long> {
    Optional<ReportDraft> findByAdoptionId(Long adoptionId);
}
