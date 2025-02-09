package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NonexistentException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void postCorrectFilm() {
        Film film = new Film();
        film.setName("Correct film");
        film.setDescription("Film with all correct fields");
        film.setReleaseDate(LocalDate.of(2020, 12, 31));
        film.setDuration(200L);
        Assertions.assertEquals(film.getName(), filmController.addFilm(film).getName());
    }

    @Test
    void postFilmWithNullName() {
        Film film = new Film();
        film.setDescription("Null film name Description");
        film.setReleaseDate(LocalDate.of(2020, 12, 31));
        film.setDuration(200L);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        String expectedMessage = "Название фильма не может быть пустым";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void postFilmWithBlankName() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Blank film name Description");
        film.setReleaseDate(LocalDate.of(2020, 12, 31));
        film.setDuration(200L);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        String expectedMessage = "Название фильма не может быть пустым";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void postFilmWithManySymbolsInDescription() {
        Film film = new Film();
        film.setName("300 Symbols");
        film.setDescription("Film300Symbols Film300Symbols Film300Symbols Film300Symbols Film300Symbols " +
                "Film300Symbols Film300Symbols Film300Symbols Film300Symbols Film300Symbols Film300Symbols " +
                "Film300Symbols Film300Symbols Film300Symbols Film300Symbols Film300Symbols Film300Symbols " +
                "Film300Symbols Film300Symbols Film300Symbols ");
        film.setReleaseDate(LocalDate.of(2020, 12, 31));
        film.setDuration(200L);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        String expectedMessage = "Максимальная длина описания не должна превышать 200 символов";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void postVeryOldFilm() {
        Film film = new Film();
        film.setName("Very Old Film");
        film.setDescription("This film of 27 december 1895 year");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(200L);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        String expectedMessage = "Дата релиза не можеть быть раньше 28.12.1895";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void postFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Negative Duration Film");
        film.setDescription("Film -200 minutes duration");
        film.setReleaseDate(LocalDate.of(2020, 12, 27));
        film.setDuration(-200L);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        String expectedMessage = "Некорректная продолжительность фильма";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateFilmWithCorrectId() {
        Film film = new Film();
        film.setName("Correct film");
        film.setDescription("Film with all correct fields");
        film.setReleaseDate(LocalDate.of(2020, 12, 31));
        film.setDuration(200L);
        filmController.addFilm(film);
        film.setId(1L);
        film.setName("Updated film name");
        Assertions.assertEquals("Updated film name", filmController.updateFilm(film).getName());
    }

    @Test
    void updateFilmWithIncorrectId() {
        Film film = new Film();
        film.setName("Correct film");
        film.setDescription("Film with all correct fields");
        film.setReleaseDate(LocalDate.of(2020, 12, 31));
        film.setDuration(200L);
        filmController.addFilm(film);
        film.setId(150L);
        film.setName("Updated film name");
        Exception exception = Assertions.assertThrows(NonexistentException.class, () -> filmController.updateFilm(film));
        String expectedMessage = "Фильм с ID 150 не найден";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}
