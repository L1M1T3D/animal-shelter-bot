package ru.skypro.animalshelter.dto;

import ru.skypro.animalshelter.model.AdoptionStatus;
import java.time.LocalDateTime;

/** Информация об испытательном сроке. */
public record AdoptionResponse(Long id, Long animalId, Long telegramId,
                               AdoptionStatus status, LocalDateTime startedAt,
                               LocalDateTime endsAt) { }
