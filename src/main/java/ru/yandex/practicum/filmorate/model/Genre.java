package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Genre {
    @Min(1)
    @Max(6)
    private int id;
    String name;
}
