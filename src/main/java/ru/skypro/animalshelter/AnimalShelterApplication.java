package ru.skypro.animalshelter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Точка входа приложения приюта и фоновых уведомлений. */
@SpringBootApplication
@EnableScheduling
public class AnimalShelterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnimalShelterApplication.class, args);
    }
}
