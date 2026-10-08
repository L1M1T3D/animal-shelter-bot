
package ru.skypro.animalshelter.dto;

import java.time.Instant;

/**
 * Информация об ошибке выполнения HTTP-запроса.
 */
public record ErrorResponse(
        String message,
        Instant timestamp
) {
}
