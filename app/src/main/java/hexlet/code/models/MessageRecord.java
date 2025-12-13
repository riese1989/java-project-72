package hexlet.code.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageRecord {
    OK(1, "Страница успешно добавлена"),
    NOT(2, "Страница уже существует");

    private final int id;
    private final String message;
}
