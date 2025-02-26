package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    ResponseEntity<User> addUsers(User user);

    ResponseEntity<User> updateUser(User user);
}
