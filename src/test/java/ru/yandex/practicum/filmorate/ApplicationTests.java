package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.dao.GenreDao;
import ru.yandex.practicum.filmorate.dal.dao.MpaDao;
import ru.yandex.practicum.filmorate.dal.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.exception.IncorrectLikeException;
import ru.yandex.practicum.filmorate.exception.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreDao.class, GenreRowMapper.class, MpaDao.class,
        MpaRowMapper.class, FilmService.class, UserService.class, UserDbStorage.class, UserRowMapper.class})
class ApplicationTests {
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
    private final UserDbStorage userStorage;
    private final UserService userService;
    private final MpaDao mpaDao;
    @Autowired
    private GenreDao genreDao;

    @BeforeEach
    public void addFilmsToDb() {
        Film film = new Film();
        film.setName("Film 1");
        film.setDescription("Description of Film 1");
        film.setReleaseDate(LocalDate.of(2019, 1, 1));
        film.setDuration(90);
        filmStorage.addFilm(film);
        film.setName("Film 2");
        film.setDescription("Description of Film 2");
        film.setReleaseDate(LocalDate.of(2000, 2, 2));
        film.setDuration(180);
        filmStorage.addFilm(film);
        User user = new User();
        user.setName("Name1");
        user.setLogin("login1");
        user.setEmail("email1@mail.com");
        user.setBirthday(LocalDate.of(1990, 2, 22));
        userStorage.addUsers(user);
        user.setName("Name2");
        user.setLogin("login2");
        user.setEmail("email2@mail.com");
        user.setBirthday(LocalDate.of(1985, 3, 30));
        userStorage.addUsers(user);
    }

    @Test
    public void testGetAllFilms() {
        List<Film> films = filmStorage.getFilms();
        Assertions.assertEquals(2, films.size());
    }

    @Test
    public void testAddFilm() {
        Film film = createCorrectFilm();
        Film result = filmStorage.addFilm(film);
        Assertions.assertEquals(3, result.getId());
        Assertions.assertEquals("Test Film", result.getName());
    }

    @Test
    public void testAddFilmWithNullName() {
        Film film = createCorrectFilm();
        film.setName(null);
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> filmStorage.addFilm(film));
    }

    @Test
    public void testAddFilmWithLongDescription() {
        Film film = createCorrectFilm();
        film.setDescription("ABRACADABRA ABRACADABRA ABRACADABRA ABRACADABRA ABRACADABRA ABRACADABRA ABRACADABRA " +
                "ABRACADABRA ABRACADABRA ABRACADABRA ABRACADABRA ABRACADABRA ABRACADABRA ABRACADABRA ABRACADABRA " +
                "ABRACADABRA ABRACADABRA ");
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> filmStorage.addFilm(film));
    }

    @Test
    public void testUpdateFilmWithIncorrectId() {
        Film film = filmStorage.getFilmById(1);
        film.setId(99L);
        Assertions.assertThrows(NotFoundException.class, () -> filmStorage.updateFilm(film));
    }

    @Test
    public void testUpdateFilm() {
        Film film = createCorrectFilm();
        Film result = filmStorage.addFilm(film);
        Assertions.assertEquals(3, result.getId());
        result.setName("Updated FilmName");
        filmStorage.updateFilm(result);
        Assertions.assertEquals("Updated FilmName", filmStorage.getFilmById(3).getName());
    }

    @Test
    public void testGetFilmById() {
        Film film = filmStorage.getFilmById(2);
        Assertions.assertEquals(2, film.getId());
        Assertions.assertEquals("Film 2", film.getName());
    }

    @Test
    public void testLikeFilm() {
        filmService.likeFilm(1L, 1L);
        filmService.likeFilm(1L, 2L);
        Film film = filmStorage.getFilmById(1);
        Assertions.assertEquals(2, film.getLikes());
    }

    @Test
    public void testLikeFilmByOneUserTwice() {
        filmService.likeFilm(1L, 1L);
        Assertions.assertThrows(IncorrectLikeException.class, () -> filmService.likeFilm(1L, 1L));
    }

    @Test
    public void testLikeFilmByIncorrectUser() {
        Assertions.assertThrows(NotFoundException.class, () -> filmService.likeFilm(1L, 9L));
    }

    @Test
    public void testLikeIncorrectFilmByCorrectUser() {
        Assertions.assertThrows(NotFoundException.class, () -> filmService.likeFilm(9L, 1L));
    }

    @Test
    public void testRemoveLikeFilm() {
        filmService.likeFilm(1L, 1L);
        filmService.likeFilm(1L, 2L);
        Film film = filmStorage.getFilmById(1);
        Assertions.assertEquals(2, film.getLikes());
        filmService.removeLike(1L, 1L);
        film = filmStorage.getFilmById(1);
        Assertions.assertEquals(1, film.getLikes());
    }

    @Test
    public void testRemoveLikeFilmByUserWhichNotLikes() {
        filmService.likeFilm(1L, 1L);
        Assertions.assertThrows(IncorrectLikeException.class, () -> filmService.removeLike(1L, 2L));
    }

    @Test
    public void testGetMostPopularFilms() {
        filmStorage.addFilm(createCorrectFilm());
        filmService.likeFilm(1L, 1L);
        filmService.likeFilm(3L, 1L);
        filmService.likeFilm(3L, 2L);
        List<Film> popularFilms = filmService.getMostPopularFilms(2);
        Assertions.assertEquals(2, popularFilms.size());
        Assertions.assertEquals(2, popularFilms.get(0).getLikes());
        Assertions.assertEquals(3, popularFilms.get(0).getId());
        Assertions.assertEquals(1, popularFilms.get(1).getLikes());
        Assertions.assertEquals(1, popularFilms.get(1).getId());
    }

    @Test
    public void testGetMpaByFilmId() {
        Film film = filmStorage.getFilmById(1);
        film.setMpa(mpaDao.getRatingById(2));
        filmStorage.updateFilm(film);
        Mpa mpa = mpaDao.getRatingByFilmId(1);
        Assertions.assertEquals("PG", mpa.getName());
    }

    @Test
    public void testGetGenresByFilmId() {
        Film film = filmStorage.getFilmById(1);
        film.setGenres(genreDao.getAllGenres());
        filmStorage.updateFilm(film);
        List<Genre> genres = genreDao.getGenresByFilmId(1);
        Assertions.assertEquals(6, genres.size());
        Assertions.assertEquals("Драма", genres.get(1).getName());
    }

    @Test
    public void testUserCreation() {
        User user = getCorrectUser();
        User result = userStorage.addUsers(user);
        assertThat(result)
                .isNotNull();
        Assertions.assertEquals(3, result.getId());
    }

    @Test
    public void testFindUserById() {
        User user = userStorage.getUserById(1);
        Assertions.assertEquals("login1", user.getLogin());
        Assertions.assertEquals("email1@mail.com", user.getEmail());
        Assertions.assertEquals("Name1", user.getName());
    }

    @Test
    public void testGetAllUsers() {
        userStorage.addUsers(getCorrectUser());
        List<User> result = userStorage.getAllUsers();
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void testUserUpdate() {
        User user = userStorage.getUserById(2);
        Assertions.assertEquals("Name2", user.getName());
        Assertions.assertEquals("email2@mail.com", user.getEmail());
        user.setName("Updated Name");
        user.setEmail("updated@email.com");
        userStorage.updateUser(user);
        User result = userStorage.getUserById(2);
        Assertions.assertEquals("Updated Name", result.getName());
        Assertions.assertEquals("updated@email.com", result.getEmail());
    }

    @Test
    public void testFriendship() {
        userService.addToFriends(1, 2);
        User user = userStorage.getUserById(1);
        User friend = userStorage.getUserById(2);
        Assertions.assertEquals(1, user.getFriends().size());
        Assertions.assertEquals(2, user.getFriends().getFirst());
        Assertions.assertEquals(0, friend.getFriends().size());
        List<User> friends = userService.getFriends(1);
        Assertions.assertEquals(1, friends.size());
        Assertions.assertEquals("Name2", friends.getFirst().getName());
        userStorage.addUsers(getCorrectUser());
        userService.addToFriends(3, 2);
        List<User> commonFriends = userService.getCommonFriends(1, 3);
        Assertions.assertEquals(1, commonFriends.size());
        Assertions.assertEquals("Name2", commonFriends.getFirst().getName());
        userService.deleteFromFriends(1, 2);
        user = userStorage.getUserById(1);
        Assertions.assertEquals(0, user.getFriends().size());
    }

    private User getCorrectUser() {
        User user = new User();
        user.setLogin("Userlogin");
        user.setEmail("user@mail.ru");
        user.setName("Innokentiy");
        user.setBirthday(LocalDate.of(2020, 12, 31));
        return user;
    }

    private Film createCorrectFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description of Test Film");
        film.setReleaseDate(LocalDate.of(2019, 3, 3));
        film.setDuration(120);
        return film;
    }

}
