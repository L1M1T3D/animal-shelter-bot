package ru.skypro.animalshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.animalshelter.model.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с ежедневными отчетами об усыновленных животных.
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByAdoptionIdAndReportDay(Long adoptionId, LocalDate day);

    List<Report> findByAdoptionIdOrderByCreatedAtDesc(Long adoptionId);

    Optional<Report> findFirstByAdoptionIdOrderByCreatedAtDesc(Long adoptionId);
}
