package hexlet.code.dto.urls;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class UrlCheckDto {
    private Long id;
    private Integer codeAnswer;
    private String title;
    private String h1;
    private String description;
    private Timestamp dateCheck;
}
