package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {
	public static final String FILMS_URL = "http://localhost:8080/films";
	public static final String USERS_URL = "http://localhost:8080/users";

@BeforeAll
	public static void setup() {
		SpringApplication.run(FilmorateApplication.class);
	}

	@Test
	void correctFilmLoad() {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		Film film = new Film();
		film.setName("Film 1");
		film.setDescription("Film 1 Description");
		film.setReleaseDate(LocalDate.of(2020, 12, 31));
		film.setDuration(200L);
		ResponseEntity<String> response = testRestTemplate.postForEntity(FILMS_URL, film, String.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Неверный код ответа.");
	}

	@Test
	void incorrectFilmLoad() {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		Film film = new Film();
		film.setName("");
		film.setDescription("Film 1 Description");
		film.setReleaseDate(LocalDate.of(2020, 12, 31));
		film.setDuration(200L);
		ResponseEntity<String> response = testRestTemplate.postForEntity(FILMS_URL, film, String.class);
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Неверный код ответа.");
	}

	@Test
	void correctUserLoad() {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		User user = new User();
		user.setLogin("Userlogin");
		user.setEmail("user@mail.ru");
		user.setName("Innokentiy");
		user.setBirthday(LocalDate.of(2000, 12, 31));
		ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Неверный код ответа.");
	}

	@Test
	void incorrectUserLoad() {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		User user = new User();
		user.setLogin("");
		user.setEmail("user@mail.ru");
		user.setName("Innokentiy");
		user.setBirthday(LocalDate.of(2000, 12, 31));
		ResponseEntity<String> response = testRestTemplate.postForEntity(USERS_URL, user, String.class);
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Неверный код ответа.");
	}

}
