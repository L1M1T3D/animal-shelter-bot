package ru.skypro.animalshelter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Полный отчёт, передаваемый сотрудником через REST API. */
public record ReportRequest(@NotNull Long adoptionId,
                            @NotBlank @Size(max = 2000) String description,
                            @NotBlank String photoFileId) { }
