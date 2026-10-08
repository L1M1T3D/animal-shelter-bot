
package ru.skypro.animalshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.animalshelter.model.Adopter;

import java.util.Optional;

/**
 * Репозиторий для хранения пользователей Telegram.
 */
public interface AdopterRepository extends JpaRepository<Adopter, Long> {

    /**
     * Ищет пользователя по идентификатору Telegram.
     *
     * @param telegramId идентификатор пользователя
     * @return Optional с найденным пользователем
     */
    Optional<Adopter> findByTelegramId(Long telegramId);
}
