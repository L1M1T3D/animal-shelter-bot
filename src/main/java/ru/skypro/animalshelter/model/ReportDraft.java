package ru.skypro.animalshelter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Черновик, позволяющий прислать фотографию и текст в разных сообщениях. */
@Entity
@Table(name = "report_drafts")
public class ReportDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adoption_id", nullable = false, unique = true)
    private Adoption adoption;

    @Column(length = 2000)
    private String description;

    @Column(name = "photo_file_id", length = 300)
    private String photoFileId;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ReportDraft() { }

    public ReportDraft(Adoption adoption, LocalDateTime now) {
        this.adoption = adoption;
        this.updatedAt = now;
    }

    public Long getId() { return id; }
    public Adoption getAdoption() { return adoption; }
    public String getDescription() { return description; }
    public String getPhotoFileId() { return photoFileId; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /** Сбрасывает недосланный отчёт с предыдущего дня. */
    public void reset(LocalDateTime now) {
        description = null;
        photoFileId = null;
        updatedAt = now;
    }

    /** Принимает текстовую часть отчёта. */
    public void setDescription(String text, LocalDateTime now) {
        description = text;
        updatedAt = now;
    }

    /** Принимает фотографию по идентификатору Telegram. */
    public void setPhotoFileId(String fileId, LocalDateTime now) {
        photoFileId = fileId;
        updatedAt = now;
    }

    public boolean isComplete() {
        return description != null && !description.isBlank()
                && photoFileId != null && !photoFileId.isBlank();
    }
}
