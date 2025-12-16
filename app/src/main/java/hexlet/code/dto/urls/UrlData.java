package hexlet.code.dto.urls;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UrlData {
    private Integer id;
    private String nameUrl;
    private Timestamp lastCheck;
    private Integer codeAnswer;
}
