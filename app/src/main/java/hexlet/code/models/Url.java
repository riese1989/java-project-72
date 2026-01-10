package hexlet.code.models;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@ToString
public final class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;
}
