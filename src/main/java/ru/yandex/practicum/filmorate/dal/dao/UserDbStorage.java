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
    private static final String GET_ALL_USERS = "select * from users";
    private static final String GET_USER_BY_ID = "select * from users where user_id = ";
    private static final String ADD_USER = "INSERT INTO users (email, name, login, birthday) VALUES (?, ?, ?, ?);";
    private final String UPDATE_USER = "UPDATE users SET email = ?, name = ?, login = ?, birthday = ? WHERE user_id = ?;";

    public List<User> getAllUsers() {
        return jdbc.query(GET_ALL_USERS, userRowMapper);
    }

    public User addUsers(User user) {
        jdbc.update(ADD_USER, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
        return jdbc.queryForObject("SELECT * FROM users ORDER BY user_id DESC LIMIT 1;", userRowMapper);
    }

    public User updateUser(User user) {
        if (!isUserExist(user.getId())) {
            throw new NotFoundException("Пользователь с ID: " + user.getId() + "не найден");
        }
        jdbc.update(UPDATE_USER, user.getEmail(), user.getName(), user.getLogin(), Date.valueOf(user.getBirthday()), user.getId());
        return getUserById(user.getId());
    }

    public User getUserById(long id) {
        if (!isUserExist(id)) {
            return null;
        }
        return jdbc.queryForObject(GET_USER_BY_ID + id, userRowMapper);
    }

    public List<User> getFriends(long id) {
        String sql =    "SELECT DISTINCT u.* " +
                        "FROM users u " +
                        "JOIN friends f ON u.user_id = f.friending_user_id OR u.user_id = f.friended_user_id" +
                        "WHERE f.accepted = true" +
                        "AND (f.friending_user_id = ? OR f.friended_user_id = ?)";
        return jdbc.query(GET_ALL_USERS, userRowMapper);

    }

    private boolean isUserExist(Long userId) {
        String sql = "select count(*) from users where user_id = ?;";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        if (count > 0) {
            return true;
        }
        return false;
    }
}
