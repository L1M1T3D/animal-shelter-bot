
package ru.skypro.animalshelter.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.skypro.animalshelter.model.Animal;
import ru.skypro.animalshelter.model.Species;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория животных.
 */
@DataJpaTest
class AnimalRepositoryTest {

    @Autowired
    private AnimalRepository repository;

    @Test
    void savesAndFindsAnimalBySpecies() {
        Animal animal = new Animal(
                "Луна",
                Species.CAT,
                true
        );

        repository.saveAndFlush(animal);

        assertThat(repository.findBySpecies(Species.CAT))
                .extracting(Animal::getName)
                .contains("Луна");
    }
}
