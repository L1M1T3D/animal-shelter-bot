
package ru.skypro.animalshelter.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.animalshelter.model.Adopter;
import ru.skypro.animalshelter.repository.AdopterRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты регистрации пользователей Telegram.
 */
@ExtendWith(MockitoExtension.class)
class AdopterServiceTest {

    @Mock
    private AdopterRepository repository;

    @InjectMocks
    private AdopterService service;

    @Test
    void savesNewTelegramUser() {
        when(repository.findByTelegramId(101L))
                .thenReturn(Optional.empty());

        service.register(101L, "anna");

        verify(repository).save(any(Adopter.class));
    }

    @Test
    void refreshesChangedUsername() {
        Adopter adopter = new Adopter(101L, "old");

        when(repository.findByTelegramId(101L))
                .thenReturn(Optional.of(adopter));

        service.register(101L, "new");

        assertThat(adopter.getUsername()).isEqualTo("new");
        verify(repository).save(adopter);
    }

    @Test
    void doesNotDuplicateExistingUser() {
        Adopter adopter = new Adopter(101L, "anna");

        when(repository.findByTelegramId(101L))
                .thenReturn(Optional.of(adopter));

        service.register(101L, "anna");

        verify(repository, never()).save(any());
    }

    @Test
    void keepsUsernameWhenNewNameIsNull() {
        Adopter adopter = new Adopter(101L, "anna");

        when(repository.findByTelegramId(101L))
                .thenReturn(Optional.of(adopter));

        service.register(101L, null);

        assertThat(adopter.getUsername()).isEqualTo("anna");
        verify(repository, never()).save(any());
    }
}
