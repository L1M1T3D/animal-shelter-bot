package ru.skypro.animalshelter.dto;

import jakarta.validation.constraints.NotNull;

/** Данные для оформления передачи питомца. */
public record AdoptionRequest(@NotNull Long animalId, @NotNull Long telegramId) { }
