
package ru.skypro.animalshelter.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.animalshelter.dto.AnimalRequest;
import ru.skypro.animalshelter.dto.AnimalResponse;
import ru.skypro.animalshelter.exception.AnimalNotFoundException;
import ru.skypro.animalshelter.model.Animal;
import ru.skypro.animalshelter.model.Species;
import ru.skypro.animalshelter.repository.AnimalRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты сервиса животных.
 */
@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository repository;

    @InjectMocks
    private AnimalService service;

    @Test
    void createReturnsGeneratedId() {
        Animal animal = mock(Animal.class);

        when(animal.getId()).thenReturn(17L);
        when(repository.save(any(Animal.class))).thenReturn(animal);

        AnimalRequest request = new AnimalRequest(
                "Рекс",
                Species.DOG,
                true
        );

        assertThat(service.create(request)).isEqualTo(17L);
    }

    @Test
    void getByIdMapsEntity() {
        Animal animal = new Animal("Луна", Species.CAT, true);

        when(repository.findById(4L))
                .thenReturn(Optional.of(animal));

        AnimalResponse response = service.getById(4L);

        assertThat(response.name()).isEqualTo("Луна");
        assertThat(response.species()).isEqualTo(Species.CAT);
        assertThat(response.available()).isTrue();
    }

    @Test
    void missingAnimalThrowsException() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(AnimalNotFoundException.class);
    }

    @Test
    void getAllWithoutFilterUsesFindAll() {
        Animal animal = new Animal("Барс", Species.CAT, true);

        when(repository.findAll())
                .thenReturn(List.of(animal));

        assertThat(service.getAll(null)).hasSize(1);

        verify(repository).findAll();
    }

    @Test
    void getAllWithFilterUsesSpecies() {
        Animal animal = new Animal("Рекс", Species.DOG, true);

        when(repository.findBySpecies(Species.DOG))
                .thenReturn(List.of(animal));

        assertThat(service.getAll(Species.DOG)).hasSize(1);

        verify(repository).findBySpecies(Species.DOG);
    }

    @Test
    void updateReplacesAnimalState() {
        Animal animal = new Animal("Старое", Species.DOG, true);

        when(repository.findById(2L))
                .thenReturn(Optional.of(animal));

        when(repository.save(animal))
                .thenReturn(animal);

        AnimalResponse response = service.update(
                2L,
                new AnimalRequest("Новое", Species.CAT, false)
        );

        assertThat(response.name()).isEqualTo("Новое");
        assertThat(response.species()).isEqualTo(Species.CAT);
        assertThat(response.available()).isFalse();
    }

    @Test
    void deleteExistingAnimal() {
        Animal animal = new Animal("Тим", Species.DOG, true);

        when(repository.findById(3L))
                .thenReturn(Optional.of(animal));

        service.delete(3L);

        verify(repository).delete(animal);
        verify(repository).flush();
    }

    @Test
    void deleteMissingAnimalThrows() {
        when(repository.findById(3L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(3L))
                .isInstanceOf(AnimalNotFoundException.class);

        verify(repository, never()).delete(any());
    }
}
