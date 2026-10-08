package ru.skypro.animalshelter.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.skypro.animalshelter.dto.ErrorResponse;
import ru.skypro.animalshelter.exception.*;
import java.time.Instant;

/** Единое преобразование ошибок приложения в корректные HTTP-статусы. */
@RestControllerAdvice
public class ApiExceptionHandler {
    /** Неизвестная сущность — HTTP 404. */
    @ExceptionHandler({AnimalNotFoundException.class, DomainNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    /** Некорректные данные — HTTP 400. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidJson(HttpMessageNotReadableException exception) {
        return new ErrorResponse(
                "Некорректный формат JSON",
                Instant.now()
        );
    }

    /** Конфликт бизнес-правил или БД — HTTP 409. */
    @ExceptionHandler({DomainConflictException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(RuntimeException e) {
        return new ErrorResponse("Операция не выполнена: " + e.getMessage(), Instant.now());
    }
}
