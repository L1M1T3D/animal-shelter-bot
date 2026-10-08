package ru.skypro.animalshelter.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.animalshelter.exception.DomainConflictException;
import ru.skypro.animalshelter.service.ReportService;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** MVC-тесты API ежедневных отчётов. */
@WebMvcTest(ReportController.class)
class ReportControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private ReportService service;

    @Test
    void createReturns201AndId() throws Exception {
        when(service.create(any())).thenReturn(8L);
        mvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"adoptionId\":7,\"description\":\"Хорошо\",\"photoFileId\":\"xyz\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("8"));
    }

    @Test
    void conflictReturns409() throws Exception {
        when(service.create(any())).thenThrow(new DomainConflictException("Уже отправлен"));
        mvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"adoptionId\":7,\"description\":\"Хорошо\",\"photoFileId\":\"xyz\"}"))
                .andExpect(status().isConflict());
    }
}
