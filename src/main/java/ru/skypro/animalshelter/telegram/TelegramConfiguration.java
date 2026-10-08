
package ru.skypro.animalshelter.telegram;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Telegram-бота.
 */
@Configuration
public class TelegramConfiguration {

    /**
     * Создаёт Telegram-бота при включённой интеграции.
     *
     * @param token токен от BotFather
     * @return экземпляр TelegramBot
     */
    @Bean
    @ConditionalOnProperty(
            name = "telegram.bot.enabled",
            havingValue = "true"
    )
    public TelegramBot telegramBot(
            @Value("${telegram.bot.token:}") String token
    ) {
        if (token.isBlank()) {
            throw new IllegalStateException(
                    "Set TELEGRAM_BOT_TOKEN before enabling Telegram"
            );
        }

        return new TelegramBot(token);
    }
}
