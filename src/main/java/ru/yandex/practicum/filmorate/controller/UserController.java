package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.debug("Получены данные для добавления пользователя: {}", user);
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь {} добавлен", user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User updateUser(@RequestBody User updatedUser) {
        log.debug("Получены данные для обновления пользователя: {}", updatedUser);

        if (updatedUser.getId() == null) {
            log.warn("Ошибка обновления пользователя без ID");
            throw new ValidationException("ID пользователя должен быть указан");
        }
        if (!users.containsKey(updatedUser.getId())) {
            log.warn("Попытка обновления несуществующего в базе пользователя {}", updatedUser.getId());
            throw new ValidationException("Пользователь с ID " + updatedUser.getId() + " не найден");
        }

        validateUser(updatedUser);
        users.put(updatedUser.getId(), updatedUser);
        log.info("Данные пользователя обновлены {}", updatedUser);
        return updatedUser;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Ошибка валидации: отсутствует почта");
            throw new ValidationException("Почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Ошибка валидации: указанная почта {} не содержит @", user.getEmail());
            throw new ValidationException("Почта должна содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Ошибка валидации: отсутствует логин");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: логин {} содержит пробелы", user.getLogin());
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Отсутствует имя, вместо него будет использован логин {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: указанная дата рождения {} ещё не наступила", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

}