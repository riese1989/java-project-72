package hexlet.code.dto.urls;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
public class UrlDataCheckPage {
    private Long id;
    private String name;
    private Timestamp createdAt;
    private List<UrlCheck> checks;
}
