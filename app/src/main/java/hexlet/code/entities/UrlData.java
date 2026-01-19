package hexlet.code.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class UrlData {
    private Long id;
    private String name;
    private Integer codeAnswer;
    private LocalDateTime dateLastCheck;
}
