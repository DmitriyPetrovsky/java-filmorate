package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;

    public List<User> getAllUsers() {
        return jdbc.query("SELECT * FROM users;", userRowMapper);
    }

    public User addUsers(User user) {
        String sql = "INSERT INTO users (email, name, login, birthday) VALUES (?, ?, ?, ?);";
        jdbc.update(sql, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
        return jdbc.queryForObject("SELECT * FROM users ORDER BY user_id DESC LIMIT 1;", userRowMapper);
    }

    public User updateUser(User user) {
        if (!isUserExist(user.getId())) {
            throw new NotFoundException("Пользователь с ID: " + user.getId() + "не найден");
        }
        String sql = "UPDATE users SET email = ?, name = ?, login = ?, birthday = ? WHERE user_id = ?;";
        jdbc.update(sql, user.getEmail(), user.getName(), user.getLogin(), Date.valueOf(user.getBirthday()), user.getId());
        return getUserById(user.getId());
    }

    public User getUserById(long id) {
        if (!isUserExist(id)) {
            return null;
        }
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return jdbc.queryForObject(sql, userRowMapper, id);
    }

    private boolean isUserExist(Long userId) {
        String sql = "select count(*) from users where user_id = ?;";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        return count > 0;
    }

}
