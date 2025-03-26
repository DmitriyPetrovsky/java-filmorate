package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.dal.dto.MpaDto;
import ru.yandex.practicum.filmorate.validator.MinimumDate;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class Film {
    private Long id;
    @NotBlank
    @NotEmpty
    private String name;
    @Size(max = 200)
    private String description;
    @MinimumDate
    private LocalDate releaseDate;
    @Min(1)
    private long duration;
    private Map<Long, Integer> likes = new HashMap<>();
    private Mpa mpa;
    private List<Genre> genres;
}
