package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dal.dao.GenreDao;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreDao genreDao;

    @GetMapping
    public List<GenreDto> getAllGenres() {
        return genreDao.getAllGenres();
    }

    @GetMapping("/{genreId}")
    public GenreDto getGenreById(@PathVariable int genreId) {
        return genreDao.getGenreById(genreId);
    }
}
