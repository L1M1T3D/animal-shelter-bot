
package ru.skypro.animalshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.animalshelter.model.Animal;
import ru.skypro.animalshelter.model.Species;

import java.util.List;

/**
 * Репозиторий для работы с животными в базе данных.
 */
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    /**
     * Возвращает всех животных указанного вида.
     *
     * @param species вид животного
     * @return список животных
     */
    List<Animal> findBySpecies(Species species);
}
