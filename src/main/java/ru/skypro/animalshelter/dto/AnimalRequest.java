
package ru.skypro.animalshelter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.skypro.animalshelter.model.Species;

/**
 * Данные для создания и изменения животного.
 *
 * @param name имя животного
 * @param species вид животного
 * @param available доступность для усыновления
 */
public record AnimalRequest(
        @NotBlank
        @Size(max = 120)
        String name,

        @NotNull
        Species species,

        @NotNull
        Boolean available
) {
}
