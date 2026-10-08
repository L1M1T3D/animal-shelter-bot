
package ru.skypro.animalshelter.exception;

/**
 * Исключение, возникающее при отсутствии животного в базе данных.
 */
public class AnimalNotFoundException extends RuntimeException {

    public AnimalNotFoundException(long id) {
        super("Животное с id=" + id + " не найдено");
    }
}
