package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dal.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreDao genreDao;

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получение списка доступных жанров");
        return genreDao.getAllGenres();
    }

    @GetMapping("/{genreId}")
    public Genre getGenreById(@PathVariable int genreId) {
        log.info("Получен запрос на получение жанра с ID: {}", genreId);
        return genreDao.getGenreById(genreId);
    }
}
