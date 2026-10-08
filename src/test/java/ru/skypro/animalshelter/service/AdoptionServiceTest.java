package ru.skypro.animalshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.skypro.animalshelter.dto.AdoptionRequest;
import ru.skypro.animalshelter.exception.DomainConflictException;
import ru.skypro.animalshelter.exception.DomainNotFoundException;
import ru.skypro.animalshelter.model.*;
import ru.skypro.animalshelter.repository.*;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Проверки оформления усыновления. */
class AdoptionServiceTest {
    private final AdoptionRepository adoptions = mock(AdoptionRepository.class);
    private final AnimalRepository animals = mock(AnimalRepository.class);
    private final AdopterRepository adopters = mock(AdopterRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-08T08:00:00Z"), ZoneId.of("Asia/Almaty"));
    private final AdoptionService service = new AdoptionService(adoptions, animals, adopters, clock);

    @Test
    void createsThirtyDayTrial() {
        Animal animal = new Animal("Бим", Species.DOG, true);
        Adopter adopter = new Adopter(100L, "owner");
        Adoption saved = mock(Adoption.class);
        when(saved.getId()).thenReturn(9L);
        when(animals.findById(2L)).thenReturn(Optional.of(animal));
        when(adopters.findByTelegramId(100L)).thenReturn(Optional.of(adopter));
        when(adoptions.findFirstByAdopterTelegramIdAndStatusInOrderByStartedAtDesc(eq(100L), anyList()))
                .thenReturn(Optional.empty());
        when(adoptions.save(any(Adoption.class))).thenReturn(saved);
        assertThat(service.create(new AdoptionRequest(2L, 100L))).isEqualTo(9L);
        assertThat(animal.isAvailable()).isFalse();
        verify(adoptions).save(argThat(a -> a.getEndsAt().equals(a.getStartedAt().plusDays(30))));
    }

    @Test
    void refusesUnavailableAnimal() {
        when(animals.findById(2L)).thenReturn(Optional.of(new Animal("Бим", Species.DOG, false)));
        when(adopters.findByTelegramId(100L)).thenReturn(Optional.of(new Adopter(100L, "owner")));
        assertThatThrownBy(() -> service.create(new AdoptionRequest(2L, 100L)))
                .isInstanceOf(DomainConflictException.class);
    }

    @Test
    void refusesUnregisteredOwner() {
        when(animals.findById(2L)).thenReturn(Optional.of(new Animal("Бим", Species.DOG, true)));
        when(adopters.findByTelegramId(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(new AdoptionRequest(2L, 100L)))
                .isInstanceOf(DomainNotFoundException.class);
    }

    @Test
    void getsActiveAdoption() {
        Adoption adoption = mock(Adoption.class);
        when(adoption.getEndsAt()).thenReturn(LocalDateTime.now(clock).plusDays(2));
        when(adoptions.findFirstByAdopterTelegramIdAndStatusInOrderByStartedAtDesc(eq(100L), anyList()))
                .thenReturn(Optional.of(adoption));
        assertThat(service.getActive(100L)).isSameAs(adoption);
    }

    @Test
    void rejectsExpiredAdoption() {
        Adoption adoption = mock(Adoption.class);
        when(adoption.getEndsAt()).thenReturn(LocalDateTime.now(clock).minusDays(1));
        when(adoptions.findFirstByAdopterTelegramIdAndStatusInOrderByStartedAtDesc(eq(100L), anyList()))
                .thenReturn(Optional.of(adoption));
        assertThatThrownBy(() -> service.getActive(100L))
                .isInstanceOf(DomainConflictException.class);
    }
}
