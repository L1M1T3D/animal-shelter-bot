package ru.skypro.animalshelter.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Сущность усыновления животного и его испытательного периода.
 */
@Entity
@Table(name = "adoptions")
public class Adoption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adopter_id", nullable = false)
    private Adopter adopter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AdoptionStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    protected Adoption() {
    }

    public Adoption(
            Adopter adopter,
            Animal animal,
            AdoptionStatus status,
            LocalDateTime startedAt,
            LocalDateTime endsAt
    ) {
        this.adopter = adopter;
        this.animal = animal;
        this.status = status;
        this.startedAt = startedAt;
        this.endsAt = endsAt;
    }

    public Long getId() {
        return id;
    }

    public Adopter getAdopter() {
        return adopter;
    }

    public Animal getAnimal() {
        return animal;
    }

    public AdoptionStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }
}
