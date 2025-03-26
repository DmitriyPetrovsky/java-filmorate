package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<GenreDto> {
    @Override
    public GenreDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        GenreDto genreDto = new GenreDto();
        genreDto.setId(resultSet.getInt("genre_id"));
        genreDto.setName(resultSet.getString("name"));
        return genreDto;
    }
}

