package ru.skypro.animalshelter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skypro.animalshelter.dto.AdoptionRequest;
import ru.skypro.animalshelter.dto.AdoptionResponse;
import ru.skypro.animalshelter.service.AdoptionService;
import java.util.List;

/** REST API оформления и просмотра усыновлений. */
@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {
    private final AdoptionService service;
    public AdoptionController(AdoptionService service) { this.service = service; }

    /** Создаёт усыновление и возвращает его ID. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long create(@Valid @RequestBody AdoptionRequest request) {
        return service.create(request);
    }

    /** Возвращает сведения об одном усыновлении. */
    @GetMapping("/{id}")
    public AdoptionResponse getById(@PathVariable long id) { return service.getById(id); }

    /** Возвращает все усыновления заданного Telegram-пользователя. */
    @GetMapping
    public List<AdoptionResponse> getByTelegramId(@RequestParam long telegramId) {
        return service.getByTelegramId(telegramId);
    }
}
