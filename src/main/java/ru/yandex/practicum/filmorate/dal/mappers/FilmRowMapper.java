package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.dao.GenreDao;
import ru.yandex.practicum.filmorate.dal.dao.MpaDao;
import ru.yandex.practicum.filmorate.dal.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final MpaIdRowMapper mpaIdRowMapper;
    private final JdbcTemplate jdbc;


    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("title"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getLong("duration"));
        film.setMpa(mpaIdRowMapper.mapRow(resultSet, rowNum));
        film.setGenres(genreDao.getGenresByFilmId(film.getId()));
        return film;
    }
}
