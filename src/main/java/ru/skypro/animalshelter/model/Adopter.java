package ru.skypro.animalshelter.model;

import jakarta.persistence.*;

/**
 * Пользователь Telegram, зарегистрированный в системе приюта.
 */
@Entity
@Table(name = "adopters")
public class Adopter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", nullable = false, unique = true)
    private Long telegramId;

    @Column(length = 255)
    private String username;

    protected Adopter() {
    }

    public Adopter(Long telegramId, String username) {
        this.telegramId = telegramId;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Изменяет Telegram username пользователя.
     *
     * @param username новое имя пользователя
     */
    public void changeUsername(String username) {
        this.username = username;
    }
}
