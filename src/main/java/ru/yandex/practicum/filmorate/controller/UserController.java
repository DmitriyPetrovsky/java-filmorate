package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NonexistentException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> getUsers() {
        log.info("--------------------------------------------------------------");
        log.info("Получен запрос на получение списка пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUsers(@RequestBody User user) {
        log.info("--------------------------------------------------------------");
        log.info("Получен запрос на добавление пользователей. Проверка на корректность введенных данных...");
        if (isValidUser(user)) {
            user.setId(getNextId());
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь {} успешно добавлен", user.getLogin());
        }
        return user;
    }

    public boolean isValidUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Поле e-mail пустое или не содержит \"@\", выбрасываю исключение:");
            throw new ValidationException("e-mail не корректен");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Логин пустой или содержит пробелы, выбрасываю исключение:");
            throw new ValidationException("Логин не должен содержать пробелы или быть пустым");
        }
        if (LocalDate.now().isBefore(user.getBirthday())) {
            log.error("Ошибка даты рождения! Текущая дата: {}, дата рождения пользователя: {}. Выбрасываю исключение:", LocalDate.now(), user.getBirthday());
            throw new ValidationException("Дата рождения позже текущей даты");
        }
        log.info("Проверка успешно завершена");
        return true;
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

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.info("--------------------------------------------------------------");
        log.info("Получен запрос на обновление пользователя. Проверка на корректность введенных данных...");
        if (isValidUser(newUser)) {
            log.info("Проверка наличия пользователя с ID: {} в списке...", newUser.getId());
            if (users.containsKey(newUser.getId())) {
                log.info("Всё ОК, пользователь с ID: {} найден.", newUser.getId());
                User oldUser = users.get(newUser.getId());
                oldUser.setEmail(newUser.getEmail());
                oldUser.setLogin(newUser.getLogin());
                oldUser.setBirthday(newUser.getBirthday());
                oldUser.setName(newUser.getName());
                if (newUser.getName() == null || newUser.getName().isEmpty()) {
                    oldUser.setName(oldUser.getLogin());
                }
                users.put(oldUser.getId(), oldUser);
                log.info("Данные пользователя {} успешно обновлены", newUser.getLogin());
                return oldUser;
            }
        }
        log.error("Пользователь с ID: {} в базе не найден, выбрасываю исключение:", newUser.getId());
        throw new NonexistentException("Пользователь с ID " + newUser.getId() + " не найден");
    }

}
