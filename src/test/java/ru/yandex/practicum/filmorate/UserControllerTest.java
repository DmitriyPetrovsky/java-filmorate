package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NonexistentException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTest {
    UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void postCorrectUser() {
        User user = createUser();
        Assertions.assertEquals(user.getLogin(), userController.addUsers(user).getLogin());
    }

    @Test
    void postUserWithEmptyEmail() {
        User user = createUser();
        user.setEmail("");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> userController.addUsers(user));
        String expectedMessage = "e-mail не корректен";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void postUserWithEmailWithoutAt() {
        User user = createUser();
        user.setEmail("usermail.com");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> userController.addUsers(user));
        String expectedMessage = "e-mail не корректен";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void postUserWithEmptyLogin() {
        User user = createUser();
        user.setLogin("");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> userController.addUsers(user));
        String expectedMessage = "Логин не должен содержать пробелы или быть пустым";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void postUserWithLoginContainsSpace() {
        User user = createUser();
        user.setLogin("User login");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> userController.addUsers(user));
        String expectedMessage = "Логин не должен содержать пробелы или быть пустым";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void postUserWithEmptyName() {
        User user = createUser();
        user.setName("");
        Assertions.assertEquals(user.getLogin(), userController.addUsers(user).getName());
    }

    @Test
    void postUserWithFutureDateOfBirth() {
        User user = createUser();
        user.setBirthday(LocalDate.of(2030, 12, 30));
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> userController.addUsers(user));
        String expectedMessage = "Дата рождения позже текущей даты";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateUserWithCorrectId() {
        User user = createUser();
        userController.addUsers(user);
        user.setId(1L);
        user.setLogin("UpdatedLogin");
        Assertions.assertEquals("UpdatedLogin", userController.updateUser(user).getLogin());
    }

    @Test
    void updateUserWithIncorrectId() {
        User user = createUser();
        userController.addUsers(user);
        user.setId(999L);
        Exception exception = Assertions.assertThrows(NonexistentException.class, () -> userController.updateUser(user));
        String expectedMessage = "Пользователь с ID 999 не найден";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    private User createUser() {
        User user = new User();
        user.setLogin("Userlogin");
        user.setEmail("user@mail.ru");
        user.setName("Innokentiy");
        user.setBirthday(LocalDate.of(2000, 12, 31));
        return user;
    }
}
