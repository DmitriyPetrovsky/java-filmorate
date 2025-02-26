package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public ResponseEntity<Map<String, String>> likeFilm(long filmId, long userId) {
        Film film = getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID: " + filmId + " не найден.");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден.");
        }
        Map<Long, Integer> filmLikes = film.getLikes();
        filmLikes.put(userId, 1);
        film.setLikes(filmLikes);
        filmStorage.updateFilm(film);
        return ResponseEntity.ok(Map.of("success", "Пользователь c ID: " + userId + " поставил лайк фильму " + film.getName()));
    }

    public ResponseEntity<Map<String, String>> removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID: " + filmId + " не найден.");
        }
        Map<Long, Integer> filmLikes = film.getLikes();
        if (!filmLikes.containsKey(userId)) {
            throw new NotFoundException("Пользователь c ID: " + userId + " не ставил лайк фильму " + film.getName());
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден.");
        }
        filmLikes.remove(userId);
        film.setLikes(filmLikes);
        filmStorage.updateFilm(film);
        return ResponseEntity.ok(Map.of("success", "Пользователь c ID: " + userId + " удалил лайк фильму " + film.getName()));
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> films = filmStorage.getFilms();
        Comparator<Film> byLikes = (f1, f2) -> {
            int likes1 = f1.getLikes().size();
            int likes2 = f2.getLikes().size();
            return Integer.compare(likes2, likes1);
        };
        films.sort(byLikes);
        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmById(long filmId) {
        List<Film> films = filmStorage.getFilms();
        Optional<Film> filmOpt = films.stream().filter(f -> f.getId() == filmId).findFirst();
        if (filmOpt.isPresent()) {
            log.info("Фильм с ID: {} найден!", filmId);
            return filmOpt.get();
        }
        return null;
    }
}
