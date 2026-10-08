
package ru.skypro.animalshelter.telegram;

import org.springframework.stereotype.Service;
import ru.skypro.animalshelter.dto.AnimalResponse;
import ru.skypro.animalshelter.service.AdopterService;
import ru.skypro.animalshelter.service.AnimalService;

import java.util.List;

/**
 * Сервис обработки текстовых команд Telegram-бота.
 */
@Service
public class BotMessageService {

    private static final int MAX_VISIBLE_ANIMALS = 20;

    private static final String COMMANDS =
            "Доступные команды: /start, /animals";

    private final AdopterService adopterService;
    private final AnimalService animalService;

    public BotMessageService(
            AdopterService adopterService,
            AnimalService animalService
    ) {
        this.adopterService = adopterService;
        this.animalService = animalService;
    }

    /**
     * Обрабатывает команду пользователя.
     *
     * @param telegramId Telegram ID пользователя
     * @param username имя пользователя
     * @param message текст команды
     * @return сообщение для отправки пользователю
     */
    public String handle(long telegramId, String username, String message) {
        if (message == null) {
            return COMMANDS;
        }

        return switch (message.trim()) {
            case "/start" -> {
                adopterService.register(telegramId, username);
                yield "Привет! Это бот приюта животных Астаны. " + COMMANDS;
            }

            case "/animals" -> listAnimals();

            default -> COMMANDS;
        };
    }

    private String listAnimals() {
        List<AnimalResponse> animals = animalService.getAll(null);

        if (animals.isEmpty()) {
            return "В каталоге пока нет животных. Попробуйте позже.";
        }

        StringBuilder result = new StringBuilder("Наши животные:\n");

        animals.stream()
                .limit(MAX_VISIBLE_ANIMALS)
                .forEach(animal -> result
                        .append(animal.id())
                        .append(". ")
                        .append(animal.name())
                        .append(" (")
                        .append(animal.species())
                        .append(")\n"));

        return result.toString();
    }
}
