package hexlet.code.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageRecord {
    OK(1, "Страница успешно добавлена"),
    PAGE_EXISTS_ERROR(2, "Страница уже существует"),
    SERVICE_REQUEST_ERROR(3, "Ошибка при обращении к стороннему сервису"),
    BAD_REQUEST_ERROR(4, "Некорректный запрос"),
    UNKNOWN_ERROR(5, "Неизвестная ошибка");

    private final int id;
    private final String message;
}
