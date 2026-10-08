
package ru.skypro.animalshelter.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик входящих сообщений Telegram.
 */
@Component
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramUpdatesListener implements UpdatesListener {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TelegramUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final BotMessageService messageService;

    public TelegramUpdatesListener(
            TelegramBot telegramBot,
            BotMessageService messageService
    ) {
        this.telegramBot = telegramBot;
        this.messageService = messageService;
    }

    /**
     * Начинает получение обновлений Telegram.
     */
    @PostConstruct
    public void start() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Останавливает получение обновлений при завершении приложения.
     */
    @PreDestroy
    public void stop() {
        telegramBot.removeGetUpdatesListener();
    }

    /**
     * Обрабатывает поступившие обновления.
     *
     * @param updates список обновлений Telegram
     * @return статус подтверждения обработки
     */
    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            handleOne(update);
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void handleOne(Update update) {
        Message message = update.message();

        if (message == null
                || message.chat() == null
                || message.from() == null) {
            return;
        }

        try {
            long chatId = message.chat().id();

            String answer = messageService.handle(
                    message.from().id(),
                    message.from().username(),
                    message.text()
            );

            telegramBot.execute(new SendMessage(chatId, answer));

        } catch (RuntimeException exception) {
            LOGGER.error(
                    "Could not handle Telegram message",
                    exception
            );
        }
    }
}
