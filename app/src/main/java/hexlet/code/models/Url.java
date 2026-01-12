package hexlet.code.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public final class Url {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
