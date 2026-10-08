
package ru.skypro.animalshelter.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.animalshelter.dto.AnimalResponse;
import ru.skypro.animalshelter.exception.AnimalNotFoundException;
import ru.skypro.animalshelter.model.Species;
import ru.skypro.animalshelter.service.AnimalService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты REST API для управления животными.
 */
@WebMvcTest(AnimalController.class)
class AnimalControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AnimalService service;

    private static final String BODY = """
            {
              "name": "Луна",
              "species": "CAT",
              "available": true
            }
            """;

    @Test
    void postReturnsCreatedAndId() throws Exception {
        when(service.create(any())).thenReturn(12L);

        mvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated())
                .andExpect(content().string("12"));
    }

    @Test
    void getByIdReturnsAnimal() throws Exception {
        AnimalResponse response = new AnimalResponse(
                1L,
                "Луна",
                Species.CAT,
                true
        );

        when(service.getById(1L)).thenReturn(response);

        mvc.perform(get("/api/animals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Луна"))
                .andExpect(jsonPath("$.species").value("CAT"));
    }

    @Test
    void listFiltersByRequestParam() throws Exception {
        AnimalResponse response = new AnimalResponse(
                2L,
                "Бим",
                Species.DOG,
                true
        );

        when(service.getAll(Species.DOG))
                .thenReturn(List.of(response));

        mvc.perform(get("/api/animals")
                        .param("species", "DOG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Бим"));
    }

    @Test
    void putReturnsUpdatedAnimal() throws Exception {
        AnimalResponse response = new AnimalResponse(
                1L,
                "Луна",
                Species.CAT,
                true
        );

        when(service.update(eq(1L), any()))
                .thenReturn(response);

        mvc.perform(put("/api/animals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mvc.perform(delete("/api/animals/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).delete(1L);
    }

    @Test
    void badRequestReturns400() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "species": "CAT",
                  "available": true
                }
                """;

        mvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidJsonReturns400() throws Exception {
        String invalidJson = """
                {
                  "name": "Луна",
                  "species": "UNKNOWN",
                  "available": true
                }
                """;

        mvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Некорректный формат JSON"));
    }

    @Test
    void missingAnimalReturns404() throws Exception {
        when(service.getById(123L))
                .thenThrow(new AnimalNotFoundException(123L));

        mvc.perform(get("/api/animals/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
