
package ru.skypro.animalshelter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.animalshelter.dto.AnimalRequest;
import ru.skypro.animalshelter.dto.AnimalResponse;
import ru.skypro.animalshelter.exception.AnimalNotFoundException;
import ru.skypro.animalshelter.model.Animal;
import ru.skypro.animalshelter.model.Species;
import ru.skypro.animalshelter.repository.AnimalRepository;

import java.util.List;

/**
 * Сервис для управления животными приюта.
 */
@Service
@Transactional(readOnly = true)
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * Создаёт новое животное.
     *
     * @param request данные животного
     * @return идентификатор созданного животного
     */
    @Transactional
    public long create(AnimalRequest request) {
        Animal animal = new Animal(
                request.name(),
                request.species(),
                request.available()
        );

        return animalRepository.save(animal).getId();
    }

    /**
     * Получает животное по идентификатору.
     *
     * @param id идентификатор животного
     * @return данные найденного животного
     */
    public AnimalResponse getById(long id) {
        Animal animal = findExisting(id);

        return toResponse(animal);
    }

    /**
     * Получает список животных с необязательной фильтрацией.
     *
     * @param species вид животного или null при отсутствии фильтра
     * @return список животных
     */
    public List<AnimalResponse> getAll(Species species) {
        List<Animal> animals;

        if (species == null) {
            animals = animalRepository.findAll();
        } else {
            animals = animalRepository.findBySpecies(species);
        }

        return animals.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Обновляет данные существующего животного.
     *
     * @param id идентификатор животного
     * @param request новые данные
     * @return обновлённое состояние животного
     */
    @Transactional
    public AnimalResponse update(long id, AnimalRequest request) {
        Animal animal = findExisting(id);

        animal.update(
                request.name(),
                request.species(),
                request.available()
        );

        Animal savedAnimal = animalRepository.save(animal);

        return toResponse(savedAnimal);
    }

    /**
     * Удаляет животное по идентификатору.
     *
     * @param id идентификатор животного
     */
    @Transactional
    public void delete(long id) {
        Animal animal = findExisting(id);

        animalRepository.delete(animal);
        animalRepository.flush();
    }

    private Animal findExisting(long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new AnimalNotFoundException(id));
    }

    private AnimalResponse toResponse(Animal animal) {
        return new AnimalResponse(
                animal.getId(),
                animal.getName(),
                animal.getSpecies(),
                animal.isAvailable()
        );
    }
}
