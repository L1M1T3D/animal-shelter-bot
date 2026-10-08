package ru.skypro.animalshelter.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/** Безопасная отправка уведомлений, если Telegram-интеграция включена. */
@Service
public class TelegramNotificationService {
    private final ObjectProvider<TelegramBot> telegramBot;
    public TelegramNotificationService(ObjectProvider<TelegramBot> telegramBot) {
        this.telegramBot = telegramBot;
    }

    /** Возвращает true только когда уведомление принято Telegram API. */
    public boolean send(long chatId, String text) {
        TelegramBot bot = telegramBot.getIfAvailable();
        if (bot == null) {
            return false;
        }
        try {
            return bot.execute(new SendMessage(chatId, text)).isOk();
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
