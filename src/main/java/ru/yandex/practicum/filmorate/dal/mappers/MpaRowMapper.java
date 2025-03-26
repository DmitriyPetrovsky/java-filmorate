package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.dal.dto.MpaDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<MpaDto> {
    @Override
    public MpaDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(resultSet.getInt("rating_id"));
        mpaDto.setName(resultSet.getString("name"));
        return mpaDto;
    }

}
