package ru.skypro.animalshelter.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.util.List;

/** Приём Telegram-сообщений, в том числе фотографий с подписью. */
@Component
@ConditionalOnProperty(name = "telegram.bot.enabled", havingValue = "true")
public class TelegramUpdatesListener implements UpdatesListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final BotMessageService messageService;

    public TelegramUpdatesListener(TelegramBot telegramBot, BotMessageService messageService) {
        this.telegramBot = telegramBot;
        this.messageService = messageService;
    }

    /** Подключает long polling при старте приложения. */
    @PostConstruct
    public void start() { telegramBot.setUpdatesListener(this); }

    /** Завершает long polling при остановке приложения. */
    @PreDestroy
    public void stop() { telegramBot.removeGetUpdatesListener(); }

    /** Подтверждает обработанные Telegram-обновления. */
    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            handleOne(update);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void handleOne(Update update) {
        Message message = update.message();
        if (message == null || message.chat() == null || message.from() == null) {
            return;
        }
        try {
            long chatId = message.chat().id();
            long telegramId = message.from().id();
            PhotoSize[] photos = message.photo();
            String answer;
            if (photos != null && photos.length > 0) {
                String fileId = photos[photos.length - 1].fileId();
                answer = messageService.handlePhoto(telegramId, fileId, message.caption());
            } else {
                answer = messageService.handle(telegramId,
                        message.from().username(), message.text());
            }
            telegramBot.execute(new SendMessage(chatId, answer));
        } catch (RuntimeException e) {
            LOGGER.error("Could not process Telegram update", e);
        }
    }
}
