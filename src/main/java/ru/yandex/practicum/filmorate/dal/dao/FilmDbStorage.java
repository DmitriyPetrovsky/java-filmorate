package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.dal.dto.MpaDto;
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
    private final String GET_ALL_FILMS = "select * from films";
    private final String GET_FILM_BY_ID = "select * from films where film_id = ?;";
    private final String ADD_FILM = "INSERT INTO films (title, description, release_date, duration) VALUES (?, ?, ?, ?);";
    private final String UPDATE_FILM = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";
    private final String ADD_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";
    private final String SET_MPA = "UPDATE films SET rating = ? WHERE film_id = ?";

    @Override
    public List<Film> getFilms() {
        return jdbc.query(GET_ALL_FILMS, filmRowMapper);
    }

    @Override
    public Film addFilm(Film film) {
        jdbc.update(ADD_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        Long filmId = jdbc.queryForObject("SELECT film_id FROM films ORDER BY film_id DESC LIMIT 1;", Long.class);
        film.setId(filmId);
        addGenres(film);
        setMpa(film);
        Film result = jdbc.queryForObject("SELECT * FROM films WHERE film_id = ?;", filmRowMapper, filmId);
        return result;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!isFilmExist(film.getId())) {
            throw new NotFoundException("Фильм с ID: " + film.getId() + " не найден");
        }
        jdbc.update(UPDATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(long id) {
        return jdbc.queryForObject(GET_FILM_BY_ID, filmRowMapper, id);
    }

    private boolean isFilmExist(Long filmId) {
        String sql = "select count(*) from films where film_id = ?;";
        Integer count = jdbc.queryForObject(sql, Integer.class, filmId);
        if (count > 0) {
            return true;
        }
        return false;
    }

    private void addGenres(Film film) {
        List<Genre> genres = film.getGenres();
        if (genres != null) {
            genres.forEach(genre -> {
                if (genre.getId() < 1 || genre.getId() > jdbc.queryForObject("SELECT COUNT(*) FROM genre;", Integer.class)) {
                    throw new NotFoundException("Некорректный индекс жанра");
                }
                jdbc.update(ADD_FILM_GENRE, film.getId(), genre.getId());}
            );
        }
    }

    private void setMpa(Film film) {
        Mpa mpa = film.getMpa();
        if (mpa != null && mpa.getId() <= 5 && mpa.getId() > 0) {
            jdbc.update(SET_MPA, film.getMpa().getId(), film.getId());
        }else if (mpa == null) {
            return;
        } else {
            throw new NotFoundException("Некорректный идентификатор рейтинга");
        }
    }
}
