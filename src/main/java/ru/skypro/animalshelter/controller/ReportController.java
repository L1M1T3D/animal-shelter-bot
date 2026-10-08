package ru.skypro.animalshelter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skypro.animalshelter.dto.ReportRequest;
import ru.skypro.animalshelter.dto.ReportResponse;
import ru.skypro.animalshelter.service.ReportService;
import java.util.List;

/** REST API получения и создания ежедневных отчётов. */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService service;
    public ReportController(ReportService service) { this.service = service; }

    /** Принимает полный отчёт и возвращает его ID. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long create(@Valid @RequestBody ReportRequest request) {
        return service.create(request);
    }

    /** Возвращает один отчёт по ID. */
    @GetMapping("/{id}")
    public ReportResponse getById(@PathVariable long id) { return service.getById(id); }

    /** Возвращает отчёты конкретного усыновления. */
    @GetMapping
    public List<ReportResponse> getByAdoptionId(@RequestParam long adoptionId) {
        return service.getByAdoptionId(adoptionId);
    }
}
