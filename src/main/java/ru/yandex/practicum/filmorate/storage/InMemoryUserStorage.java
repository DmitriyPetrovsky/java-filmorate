package ru.yandex.practicum.filmorate.storage;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    @PostConstruct
    public void init() {
        log.info("Создан экземпляр UserStorage: {}", this);
    }

    public List<User> getAllUsers() {
        log.info("Получен запрос на получение списка пользователей");
        return new ArrayList<>(users.values());
    }

    public ResponseEntity<User> addUsers(User user) {
        log.info("Получен запрос на добавление пользователей. Проверка на корректность введенных данных...");
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<User> updateUser(User user) {
        log.info("Получен запрос на обновление пользователя. Проверка на корректность введенных данных...");
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Данные пользователя {} с ID {} успешно обновлены", user.getLogin(), user.getId());
            return ResponseEntity.ok(user);
        }
        log.error("Пользователь с ID: {} не найден", user.getId());
        throw new NotFoundException("Пользователь с ID: " + user.getId() + " не найден.");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.info("Для пользователя сгенерирован ID: {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}
