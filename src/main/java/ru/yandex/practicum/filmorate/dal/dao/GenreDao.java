package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class GenreDao {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreRowMapper;

    public List<Genre> getAllGenres() {
        return jdbc.query("SELECT * FROM genre;", genreRowMapper);
    }

    public Genre getGenreById(int id) {
        if (id < 1 || id > jdbc.queryForObject("SELECT COUNT(*) FROM genre;", Integer.class)) {
            throw new NotFoundException("Некорректный индекс жанра");
        }
        return jdbc.queryForObject("SELECT * FROM genre WHERE genre_id = ?;", genreRowMapper, id);
    }

    public List<Genre> getGenresByFilmId(long filmId) {
        String sql = "SELECT * FROM genre g JOIN film_genre fg ON g.genre_id=fg.genre_id WHERE fg.film_id=?;";
        List<Genre> genres = jdbc.query(sql, genreRowMapper, filmId);
        Set<Genre> uniqueGenres = new HashSet<>(genres);
        List<Genre> sortedGenres = new ArrayList<>(uniqueGenres);
        sortedGenres.sort(Comparator.comparing(Genre::getId));
        return sortedGenres.isEmpty() ? null : sortedGenres;
    }

}
