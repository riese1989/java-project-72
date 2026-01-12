package hexlet.code.dto.urls;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UrlDataCheckPage {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private List<UrlCheckDto> checks;
}
