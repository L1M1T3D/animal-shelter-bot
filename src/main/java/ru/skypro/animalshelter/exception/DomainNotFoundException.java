package ru.skypro.animalshelter.exception;

/** Сущность не найдена в базе данных. */
public class DomainNotFoundException extends RuntimeException {
    public DomainNotFoundException(String message) { super(message); }
}
