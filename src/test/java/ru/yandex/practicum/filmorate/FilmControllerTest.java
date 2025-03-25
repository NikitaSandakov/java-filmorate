package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilmControllerTest {

    private Film film;
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        film.setName("");
        assertThatThrownBy(() -> validateFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Название фильма не может быть пустым");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        film.setDescription("A".repeat(201));
        assertThatThrownBy(() -> validateFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Описание фильма не может превышать 200 символов");
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsBefore1895() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        assertThatThrownBy(() -> validateFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата релиза не может быть раньше 28 декабря 1895 года");
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZeroOrNegative() {
        film.setDuration(0);
        assertThatThrownBy(() -> validateFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Продолжительность фильма должна быть положительным числом");

        film.setDuration(-5);
        assertThatThrownBy(() -> validateFilm(film))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Продолжительность фильма должна быть положительным числом");
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

}