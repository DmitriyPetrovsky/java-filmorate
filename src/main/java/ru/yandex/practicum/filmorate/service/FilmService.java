package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.exception.IncorrectLikeException;
import ru.yandex.practicum.filmorate.exception.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final JdbcTemplate jdbc;

    public void likeFilm(long filmId, long userId) {
        checkFilmUserCreated(filmId, userId);
        if (isLiked(filmId, userId)) {
            throw new IncorrectLikeException(String.format("Пользователь с id: %s уже поставил лайк фильму с id: %s", userId, filmId));
        }
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbc.update(sql, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        checkFilmUserCreated(filmId, userId);
        if (!isLiked(filmId, userId)) {
            throw new IncorrectLikeException(String.format("Пользователь с id: %s не ставил лайк фильму с id: %s", userId, filmId));
        }
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> films = filmStorage.getFilms();
        Comparator<Film> byLikes = (f1, f2) -> {
            int likes1 = jdbc.queryForObject("SELECT COUNT(*) FROM likes WHERE film_id = " + f1.getId(), Integer.class);
            int likes2 = jdbc.queryForObject("SELECT COUNT(*) FROM likes WHERE film_id = " + f2.getId(), Integer.class);
            return Integer.compare(likes2, likes1);
        };
        films.sort(byLikes);
        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    private boolean isLiked(long filmId, long userId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        Long count = jdbc.queryForObject(sql, Long.class, filmId, userId);
        return count != 0;
    }

    private void checkFilmUserCreated(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID: " + filmId + " не найден.");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден.");
        }
    }

}
