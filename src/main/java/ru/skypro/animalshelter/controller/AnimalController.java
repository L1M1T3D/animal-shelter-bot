
package ru.skypro.animalshelter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skypro.animalshelter.dto.AnimalRequest;
import ru.skypro.animalshelter.dto.AnimalResponse;
import ru.skypro.animalshelter.model.Species;
import ru.skypro.animalshelter.service.AnimalService;

import java.util.List;

/**
 * REST API для управления животными приюта.
 *
 * Базовый адрес: /api/animals.
 */
@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    /**
     * POST /api/animals.
     * Создаёт животное.
     *
     * @param request данные животного
     * @return идентификатор созданной записи
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long create(@Valid @RequestBody AnimalRequest request) {
        return animalService.create(request);
    }

    /**
     * GET /api/animals/{id}.
     * Получает животное по идентификатору.
     *
     * @param id идентификатор животного
     * @return данные животного
     */
    @GetMapping("/{id}")
    public AnimalResponse getById(@PathVariable long id) {
        return animalService.getById(id);
    }

    /**
     * GET /api/animals.
     * Получает список животных с необязательной фильтрацией по виду.
     *
     * @param species вид животного
     * @return список животных
     */
    @GetMapping
    public List<AnimalResponse> getAll(
            @RequestParam(required = false) Species species
    ) {
        return animalService.getAll(species);
    }

    /**
     * PUT /api/animals/{id}.
     * Обновляет животное.
     *
     * @param id идентификатор животного
     * @param request новые данные животного
     * @return обновлённое животное
     */
    @PutMapping("/{id}")
    public AnimalResponse update(
            @PathVariable long id,
            @Valid @RequestBody AnimalRequest request
    ) {
        return animalService.update(id, request);
    }

    /**
     * DELETE /api/animals/{id}.
     * Удаляет животное.
     *
     * @param id идентификатор животного
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        animalService.delete(id);
    }
}
