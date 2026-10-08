
package ru.skypro.animalshelter.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Тесты обработчика обновлений Telegram.
 */
class TelegramUpdatesListenerTest {

    private final TelegramBot bot = mock(TelegramBot.class);

    private final BotMessageService service = mock(BotMessageService.class);

    private final TelegramUpdatesListener listener =
            new TelegramUpdatesListener(bot, service);

    @Test
    void registersAndStopsPolling() {
        listener.start();
        listener.stop();

        verify(bot).setUpdatesListener(listener);
        verify(bot).removeGetUpdatesListener();
    }

    @Test
    void handlesCommandAndSendsAnswer() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        User user = mock(User.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);

        when(message.text()).thenReturn("/start");
        when(chat.id()).thenReturn(100L);
        when(user.id()).thenReturn(123L);
        when(user.username()).thenReturn("shelterguest");

        when(service.handle(123L, "shelterguest", "/start"))
                .thenReturn("Welcome");

        int result = listener.process(List.of(update));

        assertThat(result)
                .isEqualTo(UpdatesListener.CONFIRMED_UPDATES_ALL);

        verify(bot).execute(any(SendMessage.class));
    }

    @Test
    void skipsUpdatesWithoutMessages() {
        Update update = mock(Update.class);

        listener.process(List.of(update));

        verifyNoInteractions(bot, service);
    }

    @Test
    void logsExceptionsWithoutCrashing() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        User user = mock(User.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);

        when(chat.id()).thenReturn(100L);
        when(user.id()).thenReturn(123L);
        when(user.username()).thenReturn("shelterguest");
        when(message.text()).thenReturn("/start");

        when(service.handle(
                eq(123L),
                eq("shelterguest"),
                eq("/start")
        )).thenThrow(new IllegalStateException("test"));

        int result = listener.process(List.of(update));

        assertThat(result)
                .isEqualTo(UpdatesListener.CONFIRMED_UPDATES_ALL);

        verify(bot, never()).execute(any(SendMessage.class));
    }
}
