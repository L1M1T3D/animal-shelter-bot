package ru.skypro.animalshelter.model;

import jakarta.persistence.*;

/**
 * Сущность животного, находящегося в приюте.
 */
@Entity
@Table(name = "animals")
public class Animal {

    /**
     * Уникальный идентификатор животного.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Species species;

    @Column(nullable = false)
    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;

    protected Animal() {
    }

    /**
     * Создаёт новое животное.
     *
     * @param name имя животного
     * @param species вид животного
     * @param available доступность для усыновления
     */
    public Animal(String name, Species species, boolean available) {
        this.name = name;
        this.species = species;
        this.available = available;
    }

    /**
     * Обновляет данные животного.
     *
     * @param name новое имя
     * @param species новый вид
     * @param available новый статус доступности
     */
    public void update(String name, Species species, boolean available) {
        this.name = name;
        this.species = species;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Species getSpecies() {
        return species;
    }

    public boolean isAvailable() {
        return available;
    }

    public Shelter getShelter() {
        return shelter;
    }
}
