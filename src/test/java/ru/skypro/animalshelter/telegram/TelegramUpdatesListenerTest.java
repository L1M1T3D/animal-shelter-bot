package ru.skypro.animalshelter.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Проверяет передачу текстовых команд и фото с подписью. */
class TelegramUpdatesListenerTest {
    private final TelegramBot bot = mock(TelegramBot.class);
    private final BotMessageService service = mock(BotMessageService.class);
    private final TelegramUpdatesListener listener = new TelegramUpdatesListener(bot, service);

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
        when(service.handle(123L,"shelterguest","/start")).thenReturn("Welcome");
        assertThat(listener.process(List.of(update)))
                .isEqualTo(UpdatesListener.CONFIRMED_UPDATES_ALL);
        verify(bot).execute(any(SendMessage.class));
    }

    @Test
    void sendsPhotoToBotService() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        User user = mock(User.class);
        PhotoSize photo = mock(PhotoSize.class);
        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.photo()).thenReturn(new PhotoSize[]{photo});
        when(photo.fileId()).thenReturn("file123");
        when(message.caption()).thenReturn("Питомец поел");
        when(chat.id()).thenReturn(100L);
        when(user.id()).thenReturn(123L);
        when(service.handlePhoto(123L,"file123","Питомец поел")).thenReturn("Saved");
        listener.process(List.of(update));
        verify(bot).execute(any(SendMessage.class));
    }

    @Test
    void skipsUpdatesWithoutMessages() {
        listener.process(List.of(mock(Update.class)));
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
        when(service.handle(eq(123L), eq("shelterguest"), eq("/start")))
                .thenThrow(new IllegalStateException("test"));
        assertThat(listener.process(List.of(update)))
                .isEqualTo(UpdatesListener.CONFIRMED_UPDATES_ALL);
        verify(bot, never()).execute(any(SendMessage.class));
    }
}
