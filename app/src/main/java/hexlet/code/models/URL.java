package hexlet.code.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public final class URL {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public URL(String name) {
        this.name = name;
    }
}
