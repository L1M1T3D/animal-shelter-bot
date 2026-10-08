package ru.skypro.animalshelter.exception;

/** Сообщение отчёта содержит неполные или некорректные данные. */
public class BadReportException extends RuntimeException {
    public BadReportException(String message) { super(message); }
}
