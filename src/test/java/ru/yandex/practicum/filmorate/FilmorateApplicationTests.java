package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
class FilmorateApplicationTests {
    public static final String FILMS_URL = "http://localhost:8080/films";
    public static final String USERS_URL = "http://localhost:8080/users";

    @BeforeAll
    public static void setup() {
        SpringApplication.run(FilmorateApplication.class);
    }

    @Test
    void correctUserLoad() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        User user = getCorrectUser();
        ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Неверный код ответа.");
        Assertions.assertEquals("Userlogin", getUserFromResponse(response).getLogin());
    }

    @Test
    void incorrectUserLoadWithEmptyLogin() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        User user = getCorrectUser();
        user.setLogin("");
        ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле login не должно быть пустым", getErrorMessage(response));
    }

    @Test
    void incorrectUserLoadWithNullLogin() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        User user = getCorrectUser();
        user.setLogin(null);
        ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле login не должно быть пустым", getErrorMessage(response));
    }

    @Test
    void incorrectUserLoadWithNullEmail() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        User user = getCorrectUser();
        user.setEmail(null);
        ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле email не должно быть пустым", getErrorMessage(response));
    }

    @Test
    void incorrectUserLoadWithEmptyEmail() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        User user = getCorrectUser();
        user.setEmail("");
        ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле email не должно быть пустым", getErrorMessage(response));
    }

    @Test
    void incorrectUserLoadWithWrongEmail() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        User user = getCorrectUser();
        user.setEmail("usermail@.ru");
        ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле email должно иметь формат адреса электронной почты", getErrorMessage(response));
    }

    @Test
    void incorrectUserLoadWithBirthdayInFuture() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        User user = getCorrectUser();
        user.setBirthday(LocalDate.of(2040, 12, 30));
        ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле birthday должно содержать прошедшую дату", getErrorMessage(response));
    }

    @Test
    void correctUserLoadWithEmptyName() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        User user = getCorrectUser();
        user.setName("");
        user.setLogin("common");
        ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
        User userObject = getUserFromResponse(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Неверный код ответа.");
        Assertions.assertEquals("common", userObject.getName());
    }

    @Test
    void updateUserWithUnexistentID() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        User user = getCorrectUser();
        user.setId(999L);
        ResponseEntity<String> response = testRestTemplate.exchange(USERS_URL, HttpMethod.PUT, new HttpEntity<>(user), String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Неверный код ответа.");
        Assertions.assertEquals("Пользователь с ID: 999 не найден.", getErrorMessage(response));
    }

    @Test
    void correctFilmLoad() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        Film film = getCorrectFilm();
        ResponseEntity<String> response = testRestTemplate.postForEntity(FILMS_URL, film, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Неверный код ответа.");
        Assertions.assertEquals("Film 1", getFilmFromResponse(response).getName());
    }

    @Test
    void incorrectFilmLoadWithNullFilmName() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        Film film = getCorrectFilm();
        film.setName(null);
        ResponseEntity<String> response = testRestTemplate.postForEntity(FILMS_URL, film, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле name не должно быть пустым", getErrorMessage(response));
    }

    @Test
    void incorrectFilmLoadWithNullEmptyName() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        Film film = getCorrectFilm();
        film.setName("");
        ResponseEntity<String> response = testRestTemplate.postForEntity(FILMS_URL, film, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле name не должно быть пустым", getErrorMessage(response));
    }

    @Test
    void incorrectFilmLoadWithVeryLongDescription() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        Film film = getCorrectFilm();
        film.setDescription("Very Long Film Description, Very Long Film Description, Very Long Film Description, " +
                "Very Long Film Description, Very Long Film Description, Very Long Film Description, " +
                "Very Long Film Description, Very Long Film Description");
        ResponseEntity<String> response = testRestTemplate.postForEntity(FILMS_URL, film, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле description размер должен находиться в диапазоне от 0 до 200", getErrorMessage(response));
    }

    @Test
    void incorrectFilmLoadWithEarlyReleaseDate() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        Film film = getCorrectFilm();
        film.setReleaseDate(LocalDate.of(1880, 12, 31));
        ResponseEntity<String> response = testRestTemplate.postForEntity(FILMS_URL, film, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        Assertions.assertEquals("не должно быть ранее 1895-12-28", getErrorMessage(response));
    }

    @Test
    void incorrectFilmLoadWithNegativeDuration() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        Film film = getCorrectFilm();
        film.setDuration(-1L);
        ResponseEntity<String> response = testRestTemplate.postForEntity(FILMS_URL, film, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Неверный код ответа.");
        //Assertions.assertEquals("Поле duration должно быть не меньше 1", getErrorMessage(response));
    }

    @Test
    void updateFilmWithUnexistentID() throws IOException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        Film film = getCorrectFilm();
        film.setId(999L);
        ResponseEntity<String> response = testRestTemplate.exchange(FILMS_URL, HttpMethod.PUT, new HttpEntity<>(film), String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Неверный код ответа.");
        Assertions.assertEquals("Фильм с ID: 999 не найден.", getErrorMessage(response));
    }

    private User getCorrectUser() {
        User user = new User();
        user.setLogin("Userlogin");
        user.setEmail("user@mail.ru");
        user.setName("Innokentiy");
        user.setBirthday(LocalDate.of(2020, 12, 31));
        return user;
    }

    private Film getCorrectFilm() {
        Film film = new Film();
        film.setName("Film 1");
        film.setDescription("Film 1 Description");
        film.setReleaseDate(LocalDate.of(2020, 12, 31));
        film.setDuration(200L);
        return film;
    }

    private String getErrorMessage(ResponseEntity<String> response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> error = mapper.readValue(response.getBody(), Map.class);
        Optional<String> resultOpt = error.values().stream().findFirst();
        if (resultOpt.isPresent()) {
            return resultOpt.get();
        }
        return null;
    }

    private User getUserFromResponse(ResponseEntity<String> response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(response.getBody(), User.class);
    }

    private Film getFilmFromResponse(ResponseEntity<String> response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(response.getBody(), Film.class);
    }

}
