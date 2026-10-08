package ru.skypro.animalshelter.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.animalshelter.service.AdoptionService;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** MVC-тесты API оформления усыновления. */
@WebMvcTest(AdoptionController.class)
class AdoptionControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private AdoptionService service;

    @Test
    void createReturnsIdAnd201() throws Exception {
        when(service.create(any())).thenReturn(17L);
        mvc.perform(post("/api/adoptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"animalId\":3,\"telegramId\":100}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("17"));
    }

    @Test
    void validatesRequestBody() throws Exception {
        mvc.perform(post("/api/adoptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"animalId\":null,\"telegramId\":100}"))
                .andExpect(status().isBadRequest());
    }
}
