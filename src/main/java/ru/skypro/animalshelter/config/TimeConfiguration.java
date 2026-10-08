package ru.skypro.animalshelter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;
import java.time.ZoneId;

/** Настройка временной зоны приюта, пригодная для подмены в тестах. */
@Configuration
public class TimeConfiguration {
    /** Возвращает часы в часовой зоне приюта. */
    @Bean
    public Clock shelterClock(@Value("${app.time-zone:Asia/Almaty}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }
}