package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films;";
        return jdbc.query(sql, filmRowMapper);
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (title, description, release_date, duration) VALUES (?, ?, ?, ?);";
        jdbc.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        Long filmId = jdbc.queryForObject("SELECT film_id FROM films ORDER BY film_id DESC LIMIT 1;", Long.class);
        film.setId(filmId);
        addGenres(film);
        setMpa(film);
        return getFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        if (!isFilmExists(film.getId())) {
            throw new NotFoundException("Фильм с ID: " + film.getId() + " не найден");
        }
        String sql = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?;";
        jdbc.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        setMpa(film);
        addGenres(film);
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(long id) {
        if (!isFilmExists(id)) {
            throw new NotFoundException("Фильм не найден");
        }
        String sql = "SELECT * FROM films WHERE film_id = ?;";
        return jdbc.queryForObject(sql, filmRowMapper, id);
    }

    private boolean isFilmExists(Long filmId) {
        String sql = "select count(*) from films where film_id = ?;";
        Integer count = jdbc.queryForObject(sql, Integer.class, filmId);
        return count > 0;
    }

    private void addGenres(Film film) {
        List<Genre> genres = film.getGenres();
        if (genres != null) {
            genres.forEach(genre -> {
                        if (genre.getId() < 1 || genre.getId() > jdbc.queryForObject("SELECT COUNT(*) FROM genre;", Integer.class)) {
                            throw new NotFoundException("Некорректный индекс жанра");
                        }
                        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";
                        jdbc.update(sql, film.getId(), genre.getId());
                    }
            );
        }
    }

    private void setMpa(Film film) {
        Mpa mpa = film.getMpa();
        if (mpa != null && mpa.getId() <= 5 && mpa.getId() > 0) {
            String sql = "UPDATE films SET rating = ? WHERE film_id = ?;";
            jdbc.update(sql, film.getMpa().getId(), film.getId());
        } else if (mpa == null) {
            return;
        } else {
            throw new NotFoundException("Некорректный идентификатор рейтинга");
        }
    }

}
