package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.dal.mappers.GenreIdRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDao {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreRowMapper;
    private final GenreIdRowMapper genreIdRowMapper;
    private final String GET_ALL_GENRES = "select * from genre;";
    private final String GET_GENRE_BY_ID = "select * from genre where genre_id = ?;";
   // private final String GET_GENRES_BY_FILM_ID = "SELECT * FROM genre g JOIN film_genre fg ON g.genre_id=fg.genre_id WHERE fg.film_id=10;";
    private final String GET_GENRES_BY_FILM_ID = "SELECT * FROM film_genre fg WHERE fg.film_id=?;";

    public List<GenreDto> getAllGenres() {
        return jdbc.query(GET_ALL_GENRES, genreRowMapper);
    }

    public GenreDto getGenreById(int id) {
        if (id < 1 || id > jdbc.queryForObject("SELECT COUNT(*) FROM genre;", Integer.class)) {
            throw new NotFoundException("Некорректный индекс жанра");
        }
        return jdbc.queryForObject(GET_GENRE_BY_ID, genreRowMapper, id);
    }

    public List<Genre> getGenresByFilmId(long filmId) {
        List<Genre> genres = new ArrayList<>();
        genres = jdbc.query(GET_GENRES_BY_FILM_ID, genreIdRowMapper, filmId);
        if (genres.isEmpty()) {
            return null;
        }
     return genres;
    }

}
