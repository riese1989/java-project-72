package hexlet.code.dto.urls;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UrlDataDto {
    private Long id;
    private String nameUrl;
    private Integer codeAnswer;
    private Timestamp dateLastCheck;
}
