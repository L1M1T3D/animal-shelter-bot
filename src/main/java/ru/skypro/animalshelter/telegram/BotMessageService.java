package ru.skypro.animalshelter.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skypro.animalshelter.dto.AnimalResponse;
import ru.skypro.animalshelter.exception.*;
import ru.skypro.animalshelter.model.Adoption;
import ru.skypro.animalshelter.service.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Команды Telegram и ведение диалога по ежедневному отчёту. */
@Service
public class BotMessageService {
    private static final int MAX_VISIBLE_ANIMALS = 20;
    private static final String COMMANDS =
            "Команды: /start, /help, /animals, /shelter, /adopt, /care, /contact, /myadoption, /report";

    @Value("${shelter.public.address:Адрес уточняется у волонтёра}")
    private String shelterAddress = "Адрес уточняется у волонтёра";
    @Value("${shelter.public.hours:Время работы уточняется у волонтёра}")
    private String shelterHours = "Время работы уточняется у волонтёра";
    @Value("${shelter.public.contact:Контакты уточняются у волонтёра}")
    private String shelterContact = "Контакты уточняются у волонтёра";

    private final AdopterService adopterService;
    private final AnimalService animalService;
    private final AdoptionService adoptionService;
    private final ReportService reportService;

    public BotMessageService(AdopterService adopterService, AnimalService animalService,
                             AdoptionService adoptionService, ReportService reportService) {
        this.adopterService = adopterService;
        this.animalService = animalService;
        this.adoptionService = adoptionService;
        this.reportService = reportService;
    }

    /** Возвращает ответ на команду или текстовую часть отчёта. */
    public String handle(long telegramId, String username, String message) {
        if (message == null || message.isBlank()) {
            return COMMANDS;
        }
        String text = message.strip();
        try {
            return switch (text) {
                case "/start" -> {
                    adopterService.register(telegramId, username);
                    yield "Привет! Это бот приюта животных Астаны. " + COMMANDS;
                }
                case "/help" -> COMMANDS;
                case "/animals" -> listAnimals();
                case "/shelter" -> "Информация о приюте:\nАдрес: " + shelterAddress
                        + "\nРежим работы: " + shelterHours;
                case "/adopt" -> "Для знакомства с питомцем изучите /animals. "
                        + "Перед передачей животного согласуйте визит с волонтёром, "
                        + "подготовьте удостоверение личности и документы по правилам приюта.";
                case "/care" -> "Подготовьте безопасное место, воду, корм и переноску. "
                        + "Дайте животному время привыкнуть. При ухудшении здоровья обратитесь к ветеринару.";
                case "/contact" -> "Связь с приютом: " + shelterContact;
                case "/myadoption" -> showAdoption(telegramId);
                case "/report" -> beginReport(telegramId);
                default -> processOtherText(telegramId, text);
            };
        } catch (DomainConflictException | DomainNotFoundException | BadReportException e) {
            return e.getMessage();
        }
    }

    /** Обрабатывает фотографию отчёта и необязательную подпись. */
    public String handlePhoto(long telegramId, String fileId, String caption) {
        try {
            return explain(reportService.addPhoto(telegramId, fileId, caption));
        } catch (DomainConflictException | DomainNotFoundException | BadReportException e) {
            return e.getMessage();
        }
    }

    private String processOtherText(long telegramId, String text) {
        if (text.startsWith("/")) {
            return COMMANDS;
        }
        if (!reportService.hasDraft(telegramId)) {
            return "Неизвестная команда. " + COMMANDS;
        }
        return explain(reportService.addText(telegramId, text));
    }

    private String beginReport(long telegramId) {
        reportService.start(telegramId);
        return "Отчёт начат. Пришлите фото питомца и текст о его состоянии. "
                + "Можно отправить фотографию с подписью одним сообщением.";
    }

    private String showAdoption(long telegramId) {
        Adoption adoption = adoptionService.getActive(telegramId);
        return "Испытательный срок до "
                + adoption.getEndsAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                + ". Для ежедневного отчёта используйте /report.";
    }

    private String explain(ReportService.Progress progress) {
        return switch (progress) {
            case NEED_TEXT -> "Фотография получена. Теперь пришлите текст отчёта.";
            case NEED_PHOTO -> "Описание получено. Теперь пришлите фотографию питомца.";
            case COMPLETE -> "Спасибо! Полный ежедневный отчёт сохранён.";
        };
    }

    private String listAnimals() {
        List<AnimalResponse> available = animalService.getAll(null).stream()
                .filter(AnimalResponse::available)
                .limit(MAX_VISIBLE_ANIMALS)
                .toList();
        if (available.isEmpty()) {
            return "В каталоге пока нет животных. Попробуйте позже.";
        }
        StringBuilder result = new StringBuilder("Доступные питомцы:\n");
        available.forEach(animal -> result.append(animal.id()).append(". ")
                .append(animal.name()).append(" (")
                .append(animal.species()).append(")\n"));
        return result.toString();
    }
}
