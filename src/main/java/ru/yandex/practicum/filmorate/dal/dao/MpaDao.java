package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDao {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mpaRowMapper;

    public List<Mpa> getAllRatings() {
        return jdbc.query("SELECT * FROM rating;", mpaRowMapper);
    }

    public Mpa getRatingById(int id) {
        if (id <= 0 || id > 5) {
            throw new NotFoundException("Некорректный индекс рейтинга");
        }
        return jdbc.queryForObject("SELECT * FROM rating WHERE rating_id = ?;", mpaRowMapper, id);
    }

    public Mpa getRatingByFilmId(long filmId) {
        String sql = "SELECT * FROM rating JOIN films ON rating.rating_id = films.rating WHERE films.film_id=?;";
        Mpa mpa = jdbc.queryForObject(sql, mpaRowMapper, filmId);
        if (mpa == null) {
            return null;
        }
        return mpa;
    }
}
