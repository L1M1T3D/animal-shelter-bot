package ru.skypro.animalshelter.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Полный ежедневный отчёт владельца, включающий текст и фото. */
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adoption_id", nullable = false)
    private Adoption adoption;

    @Column(length = 2000)
    private String description;

    @Column(name = "photo_file_id", length = 300)
    private String photoFileId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "report_day", nullable = false)
    private LocalDate reportDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportStatus status;

    protected Report() { }

    public Report(Adoption adoption, String description, String photoFileId,
                  LocalDateTime createdAt, LocalDate reportDay, ReportStatus status) {
        this.adoption = adoption;
        this.description = description;
        this.photoFileId = photoFileId;
        this.createdAt = createdAt;
        this.reportDay = reportDay;
        this.status = status;
    }

    public Long getId() { return id; }
    public Adoption getAdoption() { return adoption; }
    public String getDescription() { return description; }
    public String getPhotoFileId() { return photoFileId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDate getReportDay() { return reportDay; }
    public ReportStatus getStatus() { return status; }
}
