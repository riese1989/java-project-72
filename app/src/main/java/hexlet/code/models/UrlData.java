package hexlet.code.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@ToString
public class UrlData {
    private Long id;
    private String name;
    private Integer codeAnswer;
    private Timestamp dateLastCheck;
}
