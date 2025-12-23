package hexlet.code.dto.urls;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UrlData {
    private Long id;
    private String nameUrl;
    private Timestamp lastCheck;
    private Integer codeAnswer;
}
