package ru.skypro.animalshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.animalshelter.model.Adoption;
import ru.skypro.animalshelter.model.AdoptionStatus;
import java.util.List;
import java.util.Optional;

/** Доступ к договорам усыновления. */
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    Optional<Adoption> findFirstByAdopterTelegramIdAndStatusInOrderByStartedAtDesc(
            Long telegramId, List<AdoptionStatus> statuses);
    List<Adoption> findByStatusIn(List<AdoptionStatus> statuses);
    List<Adoption> findByAdopterTelegramIdOrderByStartedAtDesc(Long telegramId);
}
