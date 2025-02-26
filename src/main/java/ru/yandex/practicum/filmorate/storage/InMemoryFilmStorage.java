package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    public List<Film> getFilms() {
        log.info("Получен запрос на получение списка фильмов");
        return new ArrayList<>(films.values());
    }

    public ResponseEntity<Film> addFilm(Film film) {
        log.info("Получен запрос на добавление фильма. Проверка на корректность введенных данных...");
        film.setId(getNextId());
        films.put(film.getId(), film);
        return ResponseEntity.ok(film);
    }

    public ResponseEntity<Film> updateFilm(Film film) {
        log.info("Получен запрос на обновление фильма. Проверка на корректность введенных данных...");
        if (films.containsKey(film.getId())) {
            log.info("Всё ОК, фильм с ID: {} найден.", film.getId());
            films.put(film.getId(), film);
            return ResponseEntity.ok(film);
        }
        log.error("Фильм с ID: {} не найден", film.getId());
        throw new NotFoundException("Фильм с ID: " + film.getId() + " не найден.");
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
