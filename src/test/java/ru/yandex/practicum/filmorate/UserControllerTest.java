package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserControllerTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setLogin("User123");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1995, 6, 15));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmptyOrInvalid() {
        user.setEmail("");
        assertThatThrownBy(() -> validateUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Почта не может быть пустой");

        user.setEmail("invalid-email");
        assertThatThrownBy(() -> validateUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Почта должна содержать @");
    }

    @Test
    void shouldThrowExceptionWhenLoginIsEmptyOrContainsSpaces() {
        user.setLogin("");
        assertThatThrownBy(() -> validateUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Логин не может быть пустым");

        user.setLogin("User 123");
        assertThatThrownBy(() -> validateUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Логин не должен содержать пробелы");
    }

    @Test
    void shouldUseLoginAsNameIfNameIsEmpty() {
        user.setName("");
        validateUser(user);
        assert user.getName().equals(user.getLogin());
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThatThrownBy(() -> validateUser(user))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата рождения не может быть в будущем");
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Почта должна содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

}