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
import ru.yandex.practicum.filmorate.dal.dao.MpaDao;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDao.class, MpaRowMapper.class})
public class MpaTests {
    private final MpaDao mpaDao;

    @Test
    public void testGetAllMpa() {
        List<Mpa> allRatings = mpaDao.getAllRatings();
        Assertions.assertEquals(5, allRatings.size());
        Assertions.assertEquals("PG-13", allRatings.get(2).getName());
    }

    @Test
    public void testGetMpaById() {
        Mpa mpa = mpaDao.getRatingById(2);
        Assertions.assertEquals(2, mpa.getId());
        Assertions.assertEquals("PG", mpa.getName());
    }
}
