package hexlet.code.dto.urls;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UrlData {
    private Integer id;
    private String nameUrl;
    private LocalDateTime lastCheck;
    private Integer codeAnswer;
}
