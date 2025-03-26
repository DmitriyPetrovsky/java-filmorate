package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
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

    public List<User> getFriends(long id) {
        User user = getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + id + " не найден.");
        }
        //List<User> friends = jdbc.query();
        return userStorage.getAllUsers().stream()
                .filter(u -> user.getFriends().contains(u.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + id + " не найден.");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с ID: " + friendId + " не найден.");
        }
        return userStorage.getAllUsers().stream()
                .filter(u -> user.getFriends().contains(u.getId()) && friend.getFriends().contains(u.getId()))
                .collect(Collectors.toList());
    }

    public void addToFriends(long userId, long friendId) {
        log.info("Попытка добавления в друзья пользователем {} пользователя {}", userId, friendId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь с ID: " + friendId + " не найден.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден.");
        }
        user.getFriends().add(friendId);
        //friend.getFriends().add(userId);
        userStorage.updateUser(user);
        //userStorage.updateUser(friend);
    }

    public void deleteFromFriends(long userId, long friendId) {
        log.info("Попытка удаления из друзей пользователем {} пользователя {}", userId, friendId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь с ID: " + friendId + " не найден.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден.");
        }
        friend.getFriends().remove(userId);
        user.getFriends().remove(friendId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

}
