package ru.skypro.animalshelter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.animalshelter.dto.AdoptionRequest;
import ru.skypro.animalshelter.dto.AdoptionResponse;
import ru.skypro.animalshelter.exception.DomainConflictException;
import ru.skypro.animalshelter.exception.DomainNotFoundException;
import ru.skypro.animalshelter.model.*;
import ru.skypro.animalshelter.repository.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/** Оформление передачи животного и получение испытательного срока. */
@Service
@Transactional(readOnly = true)
public class AdoptionService {
    public static final int TRIAL_DAYS = 30;
    public static final List<AdoptionStatus> ACTIVE_STATUSES =
            List.of(AdoptionStatus.TRIAL, AdoptionStatus.EXTENDED);

    private final AdoptionRepository adoptions;
    private final AnimalRepository animals;
    private final AdopterRepository adopters;
    private final Clock clock;

    public AdoptionService(AdoptionRepository adoptions, AnimalRepository animals,
                           AdopterRepository adopters, Clock clock) {
        this.adoptions = adoptions;
        this.animals = animals;
        this.adopters = adopters;
        this.clock = clock;
    }

    /** Создаёт испытательный срок и помечает животное недоступным. */
    @Transactional
    public long create(AdoptionRequest request) {
        Animal animal = animals.findById(request.animalId())
                .orElseThrow(() -> new DomainNotFoundException("Животное не найдено"));
        Adopter adopter = adopters.findByTelegramId(request.telegramId())
                .orElseThrow(() -> new DomainNotFoundException("Сначала зарегистрируйте пользователя через /start"));
        if (!animal.isAvailable()) {
            throw new DomainConflictException("Животное уже передано другому владельцу");
        }
        if (adoptions.findFirstByAdopterTelegramIdAndStatusInOrderByStartedAtDesc(
                request.telegramId(), ACTIVE_STATUSES).isPresent()) {
            throw new DomainConflictException("У владельца уже есть активный испытательный срок");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        animal.update(animal.getName(), animal.getSpecies(), false);
        animals.save(animal);
        Adoption adoption = new Adoption(adopter, animal, AdoptionStatus.TRIAL,
                now, now.plusDays(TRIAL_DAYS));
        return adoptions.save(adoption).getId();
    }

    /** Находит активное усыновление владельца. */
    public Adoption getActive(long telegramId) {
        Adoption adoption = adoptions.findFirstByAdopterTelegramIdAndStatusInOrderByStartedAtDesc(
                telegramId, ACTIVE_STATUSES)
                .orElseThrow(() -> new DomainNotFoundException("Активное усыновление не найдено"));
        if (LocalDateTime.now(clock).isAfter(adoption.getEndsAt())) {
            throw new DomainConflictException("Испытательный срок закончился; обратитесь к волонтёру");
        }
        return adoption;
    }

    /** Возвращает данные одного усыновления. */
    public AdoptionResponse getById(long id) {
        return toResponse(adoptions.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Усыновление не найдено")));
    }

    /** Возвращает историю усыновлений владельца. */
    public List<AdoptionResponse> getByTelegramId(long telegramId) {
        return adoptions.findByAdopterTelegramIdOrderByStartedAtDesc(telegramId)
                .stream().map(this::toResponse).toList();
    }

    private AdoptionResponse toResponse(Adoption a) {
        return new AdoptionResponse(a.getId(), a.getAnimal().getId(),
                a.getAdopter().getTelegramId(), a.getStatus(),
                a.getStartedAt(), a.getEndsAt());
    }
}
