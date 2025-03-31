package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaDao mpaDao;

    @GetMapping
    public List<Mpa> getAllRatings() {
        log.info("Получен запрос на получение списка всех MPA");
        return mpaDao.getAllRatings();
    }

    @GetMapping("/{mpaId}")
    public Mpa getGenreById(@PathVariable int mpaId) {
        log.info("Получен запрос на получение MPA c ID: {}", mpaId);
        return mpaDao.getRatingById(mpaId);
    }
}
