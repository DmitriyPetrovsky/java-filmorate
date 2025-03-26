package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.dao.MpaDao;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.dal.dto.MpaDto;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaDao mpaDao;

    @GetMapping
    public List<MpaDto> getAllGenres() {
        return mpaDao.getAllRatings();
    }

    @GetMapping("/{mpaId}")
    public MpaDto getGenreById(@PathVariable int mpaId) {
        return mpaDao.getRatingById(mpaId);
    }
}
