package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.dao.GenreDao;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDao.class, GenreRowMapper.class})
public class GenreTests {
    private final GenreDao genreDao;

    @Test
    public void testGetAllGenres() {
        List<Genre> allRatings = genreDao.getAllGenres();
        Assertions.assertEquals(6, allRatings.size());
        Assertions.assertEquals("Мультфильм", allRatings.get(2).getName());
    }

    @Test
    public void testGetMpaById() {
        Genre genre = genreDao.getGenreById(2);
        Assertions.assertEquals(2, genre.getId());
        Assertions.assertEquals("Драма", genre.getName());
    }
}
