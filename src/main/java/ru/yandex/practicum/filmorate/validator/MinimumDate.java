package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
@Target({ElementType.FIELD})
@Documented
public @interface MinimumDate {
    String message() default "не должно быть ранее 1895-12-28";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String value() default "1895-12-28";
}
