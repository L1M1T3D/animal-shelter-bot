package ru.skypro.animalshelter.dto;

import ru.skypro.animalshelter.model.ReportStatus;
import java.time.LocalDate;

/** Данные сохранённого ежедневного отчёта. */
public record ReportResponse(Long id, Long adoptionId, String description,
                             String photoFileId, LocalDate reportDay,
                             ReportStatus status) { }
