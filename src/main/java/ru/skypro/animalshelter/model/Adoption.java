package ru.skypro.animalshelter.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Договор о передаче питомца владельцу на испытательный срок. */
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

    @Column(name = "last_escalation_on")
    private LocalDate lastEscalationOn;

    protected Adoption() { }

    public Adoption(Adopter adopter, Animal animal, AdoptionStatus status,
                    LocalDateTime startedAt, LocalDateTime endsAt) {
        this.adopter = adopter;
        this.animal = animal;
        this.status = status;
        this.startedAt = startedAt;
        this.endsAt = endsAt;
    }

    public Long getId() { return id; }
    public Adopter getAdopter() { return adopter; }
    public Animal getAnimal() { return animal; }
    public AdoptionStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getEndsAt() { return endsAt; }
    public LocalDate getLastEscalationOn() { return lastEscalationOn; }

    /** Запоминает дату отправки уведомления волонтёру. */
    public void markEscalated(LocalDate date) { lastEscalationOn = date; }
}
