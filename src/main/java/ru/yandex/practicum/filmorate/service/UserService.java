package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getFriends(long id) {
        User user = getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + id + " не найден.");
        }
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

    public ResponseEntity<Map<String, String>> addToFriends(long userId, long friendId) {
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
        friend.getFriends().add(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
        return ResponseEntity.ok(Map.of("success", "Пользователь " + user.getName() + " успешно добавил в друзья пользователя " + friend.getName()));
    }

    public ResponseEntity<Map<String, String>> deleteFromFriends(long userId, long friendId) {
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
        return ResponseEntity.ok(Map.of("success", "Пользователь " + user.getName() + " успешно удалил из друзей пользователя " + friend.getName()));
    }

    public User getUserById(long id) {
        List<User> users = userStorage.getAllUsers();
        Optional<User> user = users.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
        if (user.isPresent()) {
            log.info("Пользователь с ID: {} найден!", id);
            return user.get();
        }
        log.error("Пользователь с ID: {} не найден!", id);
        return null;
    }
}
