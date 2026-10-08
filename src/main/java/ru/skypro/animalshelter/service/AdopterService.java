
package ru.skypro.animalshelter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.animalshelter.model.Adopter;
import ru.skypro.animalshelter.repository.AdopterRepository;

/**
 * Сервис регистрации пользователей Telegram.
 */
@Service
public class AdopterService {

    private final AdopterRepository repository;

    public AdopterService(AdopterRepository repository) {
        this.repository = repository;
    }

    /**
     * Регистрирует пользователя или обновляет его Telegram username.
     *
     * @param telegramId идентификатор пользователя Telegram
     * @param username имя пользователя Telegram
     */
    @Transactional
    public void register(long telegramId, String username) {
        repository.findByTelegramId(telegramId)
                .ifPresentOrElse(
                        adopter -> updateUsername(adopter, username),
                        () -> repository.save(new Adopter(telegramId, username))
                );
    }

    private void updateUsername(Adopter adopter, String username) {
        if (username == null || username.equals(adopter.getUsername())) {
            return;
        }

        adopter.changeUsername(username);
        repository.save(adopter);
    }
}
