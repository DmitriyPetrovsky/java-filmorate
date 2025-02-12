package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorMessageObject;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на получение списка пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public ResponseEntity<Object> addUsers(@Valid @RequestBody User user, BindingResult bindingResult) {
        log.info("Получен запрос на добавление пользователей. Проверка на корректность введенных данных...");
        if (bindingResult.hasErrors()) {
            String errMessage = ("Поле " + bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
            ErrorMessageObject errorMessageObject = new ErrorMessageObject(errMessage);
            log.error(errMessage);
            return ResponseEntity.badRequest().body(errorMessageObject);
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<Object> updateUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        log.info("Получен запрос на обновление пользователя. Проверка на корректность введенных данных...");
        if (bindingResult.hasErrors()) {
            String errMessage = ("Поле " + bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
            ErrorMessageObject errorMessageObject = new ErrorMessageObject(errMessage);
            log.error(errMessage);
            return ResponseEntity.badRequest().body(errorMessageObject);
        }
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Данные пользователя {} с ID {} успешно обновлены", user.getLogin(), user.getId());
            return ResponseEntity.ok(user);
        }
        log.error("Пользователь с ID: {} не найден", user.getId());
        ErrorMessageObject errorMessageObject = new ErrorMessageObject("Пользователь с ID " + user.getId() + " не найден");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessageObject);
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
