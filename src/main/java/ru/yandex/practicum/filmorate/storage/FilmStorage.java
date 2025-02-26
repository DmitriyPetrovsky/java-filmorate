package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    ResponseEntity<Film> addFilm(Film film);

    ResponseEntity<Film> updateFilm(Film film);


}
