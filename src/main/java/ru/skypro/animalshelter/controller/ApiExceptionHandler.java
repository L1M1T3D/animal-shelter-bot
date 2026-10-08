
package ru.skypro.animalshelter.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.skypro.animalshelter.dto.ErrorResponse;
import ru.skypro.animalshelter.exception.AnimalNotFoundException;

import java.time.Instant;

/**
 * Централизованный обработчик исключений REST API.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Возвращает 404, если животное не найдено.
     */
    @ExceptionHandler(AnimalNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(AnimalNotFoundException exception) {
        return new ErrorResponse(
                exception.getMessage(),
                Instant.now()
        );
    }

    /**
     * Возвращает 400 при ошибках валидации входных данных.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException exception) {
        return new ErrorResponse(
                "Некорректные данные животного",
                Instant.now()
        );
    }

    /**
     * Возвращает 400 при невалидном JSON.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidJson(HttpMessageNotReadableException exception) {
        return new ErrorResponse(
                "Некорректный формат JSON",
                Instant.now()
        );
    }

    /**
     * Возвращает 409 при нарушении ограничений базы данных.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(DataIntegrityViolationException exception) {
        return new ErrorResponse(
                "Нельзя выполнить операцию из-за связей с другими данными",
                Instant.now()
        );
    }
}
