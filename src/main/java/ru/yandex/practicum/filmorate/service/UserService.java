package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;
    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;

    public List<User> getFriends(long id) {
        if (!isUserExists(id)) {
            throw new NotFoundException("Пользователь с ID: " + id + " не найден.");
        }
        String sql = "select * from users u join friends f on u.user_id=f.friended_user_id where f.friending_user_id = ?;";
        return jdbc.query(sql, userRowMapper, id);
    }

    public List<User> getCommonFriends(long id, long friendId) {
        if (!isUserExists(id) || !isUserExists(friendId)) {
            throw new NotFoundException("Пользователь не найден.");
        }
        String sql = "SELECT * " +
                "FROM users u " +
                "JOIN ( " +
                "    SELECT f1.friended_user_id " +
                "    FROM friends f1 " +
                "    JOIN friends f2 ON f1.friended_user_id = f2.friended_user_id " +
                "    WHERE f1.friending_user_id = ? " +
                "    AND f2.friending_user_id = ? " +
                ") AS common_friends ON u.user_id = common_friends.friended_user_id;";
        return jdbc.query(sql, userRowMapper, id, friendId);
    }

    public void addToFriends(long userId, long friendId) {
        log.info("Попытка добавления в друзья пользователем {} пользователя {}", userId, friendId);
        if (!isUserExists(userId) || !isUserExists(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        String sql = "INSERT into friends(friending_user_id, friended_user_id) values(?, ?)";
        jdbc.update(sql, userId, friendId);
    }

    public void deleteFromFriends(long userId, long friendId) {
        log.info("Попытка удаления из друзей пользователем {} пользователя {}", userId, friendId);
        if (!isUserExists(userId) || !isUserExists(friendId)) {
            throw new NotFoundException("Пользователь не найден.");
        }
        String sql = "DELETE FROM friends WHERE friending_user_id = ? AND friended_user_id = ?;";
        jdbc.update(sql, userId, friendId);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    private boolean isUserExists(long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            return false;
        }
        return true;
    }

}
