
package ru.skypro.animalshelter.telegram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.animalshelter.dto.AnimalResponse;
import ru.skypro.animalshelter.model.Species;
import ru.skypro.animalshelter.service.AdopterService;
import ru.skypro.animalshelter.service.AnimalService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты обработки команд Telegram.
 */
@ExtendWith(MockitoExtension.class)
class BotMessageServiceTest {

    @Mock
    private AdopterService adopters;

    @Mock
    private AnimalService animals;

    @InjectMocks
    private BotMessageService service;

    @Test
    void startRegistersUser() {
        String answer = service.handle(
                101L,
                "anna",
                "/start"
        );

        verify(adopters).register(101L, "anna");

        assertThat(answer).contains("приюта");
    }

    @Test
    void animalsCommandListsAnimals() {
        AnimalResponse animal = new AnimalResponse(
                5L,
                "Бим",
                Species.DOG,
                true
        );

        when(animals.getAll(null))
                .thenReturn(List.of(animal));

        String answer = service.handle(
                101L,
                "anna",
                "/animals"
        );

        assertThat(answer).contains("Бим");
    }

    @Test
    void animalsCommandHandlesEmptyCatalog() {
        when(animals.getAll(null))
                .thenReturn(List.of());

        String answer = service.handle(
                101L,
                "anna",
                "/animals"
        );

        assertThat(answer).contains("пока нет животных");
    }

    @Test
    void unexpectedCommandReturnsHelp() {
        String answer = service.handle(
                101L,
                "anna",
                "/unknown"
        );

        assertThat(answer).contains("/start");
    }

    @Test
    void nullMessageReturnsHelp() {
        String answer = service.handle(
                101L,
                "anna",
                null
        );

        assertThat(answer).contains("/animals");
    }
}
