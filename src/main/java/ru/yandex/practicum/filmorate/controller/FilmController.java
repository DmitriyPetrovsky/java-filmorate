package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NonexistentException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);


    @GetMapping
    public List<Film> getFilms() {
        log.info("--------------------------------------------------------------");
        log.info("Получен запрос на получение списка фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("--------------------------------------------------------------");
        log.info("Получен запрос на добавление фильма. Проверка на корректность введенных данных...");
        if (isValidFilm(film)) {
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Фильм \"{}\" успешно добавлен", film.getName());
        }
        return film;
    }

    public boolean isValidFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Запрос на добавление/обновление фильма с пустым названием, выбрасываю исключение:");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("В поле \"Описание\" введено {} символов. Максимальное количество: 200, выбрасываю исключение:", film.getDescription().length());
            throw new ValidationException("Максимальная длина описания не должна превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            log.error("Дата релиза ({}) не может быть раньше ({}), выбрасываю исключение:", film.getReleaseDate(), EARLIEST_DATE);
            throw new ValidationException("Дата релиза не можеть быть раньше 28.12.1895");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма не может быть {} минут (меньше нуля), выбрасываю исключение:", film.getDuration());
            throw new ValidationException("Некорректная продолжительность фильма");
        }
        log.info("Проверка успешно завершена");
        return true;
    }

    private Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.info("Для фильма сгенерирован ID: {}", currentMaxId+1);
        return ++currentMaxId;
    }


    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("--------------------------------------------------------------");
        log.info("Получен запрос на обновление фильма. Проверка на корректность введенных данных...");
        if (isValidFilm(newFilm)) {
            log.info("Проверка наличия фильма с ID: {} в списке...", newFilm.getId());
            if (films.containsKey(newFilm.getId())) {
                log.info("Всё ОК, фильм с ID: {} найден.", newFilm.getId());
                Film oldFilm = films.get(newFilm.getId());
                oldFilm.setName(newFilm.getName());
                oldFilm.setDescription(newFilm.getDescription());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                oldFilm.setDuration(newFilm.getDuration());
                films.put(oldFilm.getId(), oldFilm);
                return oldFilm;
            }
        }
        log.error("Фильм с ID: {} в базе не найден, выбрасываю исключение:", newFilm.getId());
        throw new NonexistentException("Фильм с ID " + newFilm.getId() + " не найден");
    }
}
