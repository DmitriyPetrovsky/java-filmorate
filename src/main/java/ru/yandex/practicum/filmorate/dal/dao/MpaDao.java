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
    private final String GET_ALL_RATINGS = "select * from rating;";
    private final String GET_RATING_BY_ID = "select * from rating where rating_id = ?;";
    private final String GET_RATING_BY_FILM_ID = "select * from rating JOIN films ON rating.rating_id = films.rating WHERE films.film_id=?;";


    public List<Mpa> getAllRatings() {
        return jdbc.query(GET_ALL_RATINGS, mpaRowMapper);
    }

    public Mpa getRatingById(int id) {
        if (id <= 0 || id > 5) {
            throw new NotFoundException("Некорректный индекс рейтинга");
        }
        return jdbc.queryForObject(GET_RATING_BY_ID, mpaRowMapper, id);
    }

    public Mpa getRatingByFilmId(long film_id) {
        Mpa mpa = jdbc.queryForObject(GET_RATING_BY_FILM_ID, mpaRowMapper, film_id);
        if (mpa == null) {
            return null;
        }
        return mpa;
    }
}
