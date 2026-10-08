package ru.skypro.animalshelter.exception;

/** Операция противоречит текущему состоянию питомца или отчёта. */
public class DomainConflictException extends RuntimeException {
    public DomainConflictException(String message) { super(message); }
}
