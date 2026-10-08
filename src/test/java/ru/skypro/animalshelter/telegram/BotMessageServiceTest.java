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
import ru.skypro.animalshelter.service.AdoptionService;
import ru.skypro.animalshelter.service.ReportService;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/** Проверяет команды бота и ответы в диалоге отчёта. */
@ExtendWith(MockitoExtension.class)
class BotMessageServiceTest {
    @Mock private AdopterService adopters;
    @Mock private AnimalService animals;
    @Mock private AdoptionService adoptions;
    @Mock private ReportService reports;
    @InjectMocks private BotMessageService service;

    @Test
    void startRegistersUser() {
        String answer = service.handle(101L, "anna", "/start");
        verify(adopters).register(101L, "anna");
        assertThat(answer).contains("приюта");
    }

    @Test
    void animalsCommandListsAnimals() {
        when(animals.getAll(null)).thenReturn(List.of(
                new AnimalResponse(5L, "Бим", Species.DOG, true)));
        assertThat(service.handle(101L, "anna", "/animals")).contains("Бим");
    }

    @Test
    void animalsCommandHandlesEmptyCatalog() {
        when(animals.getAll(null)).thenReturn(List.of());
        assertThat(service.handle(101L, "anna", "/animals")).contains("пока нет животных");
    }

    @Test
    void unexpectedCommandReturnsHelp() {
        assertThat(service.handle(101L, "anna", "/unknown")).contains("/start");
    }

    @Test
    void nullMessageReturnsHelp() {
        assertThat(service.handle(101L, "anna", null)).contains("/animals");
    }

    @Test
    void startsReportAndAsksForComponents() {
        assertThat(service.handle(101L, "anna", "/report")).contains("Отчёт начат");
        verify(reports).start(101L);
    }

    @Test
    void photoThenTextProgressIsExplained() {
        when(reports.addPhoto(101L, "photo", null)).thenReturn(ReportService.Progress.NEED_TEXT);
        assertThat(service.handlePhoto(101L, "photo", null)).contains("текст");
    }

    @Test
    void helpShowsMenu() {
        assertThat(service.handle(101L, "anna", "/help")).contains("/report");
    }
}
