
package ru.skypro.animalshelter.dto;

import ru.skypro.animalshelter.model.Species;

/**
 * Данные животного, возвращаемые пользователю через API.
 */
public record AnimalResponse(
        Long id,
        String name,
        Species species,
        boolean available
) {
}
