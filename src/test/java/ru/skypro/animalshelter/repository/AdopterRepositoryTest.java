
package ru.skypro.animalshelter.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.skypro.animalshelter.model.Adopter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты пользователей Telegram.
 */
@DataJpaTest
class AdopterRepositoryTest {

    @Autowired
    private AdopterRepository repository;

    @Test
    void savesAndFindsTelegramUser() {
        Adopter adopter = new Adopter(
                455L,
                "volunteer"
        );

        repository.saveAndFlush(adopter);

        assertThat(repository.findByTelegramId(455L))
                .isPresent()
                .get()
                .extracting(Adopter::getUsername)
                .isEqualTo("volunteer");
    }
}
