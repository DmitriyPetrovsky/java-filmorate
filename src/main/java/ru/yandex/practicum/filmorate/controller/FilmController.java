package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorMessageObject;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);


    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос на получение списка фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public ResponseEntity<Object> addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        log.info("Получен запрос на добавление фильма. Проверка на корректность введенных данных...");
        if (bindingResult.hasErrors()) {
            String errMessage = ("Поле " + bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
            ErrorMessageObject errorMessageObject = new ErrorMessageObject(errMessage);
            log.error(errMessage);
            return ResponseEntity.badRequest().body(errorMessageObject);
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity<Object> updateFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        log.info("Получен запрос на обновление фильма. Проверка на корректность введенных данных...");
        if (bindingResult.hasErrors()) {
            String errMessage = ("Поле " + bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
            ErrorMessageObject errorMessageObject = new ErrorMessageObject(errMessage);
            log.error(errMessage);
            return ResponseEntity.badRequest().body(errorMessageObject);
        }
        if (films.containsKey(film.getId())) {
            log.info("Всё ОК, фильм с ID: {} найден.", film.getId());
            films.put(film.getId(), film);
            return ResponseEntity.ok(film);
        }
        log.error("Фильм с ID: {} не найден",film.getId());
        ErrorMessageObject errorMessageObject = new ErrorMessageObject("Фильм с ID " + film.getId() + " не найден");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessageObject);
}

private Long getNextId() {
    long currentMaxId = films.keySet()
            .stream()
            .mapToLong(id -> id)
            .max()
            .orElse(0);
    log.info("Для фильма сгенерирован ID: {}", currentMaxId + 1);
    return ++currentMaxId;
}
}
