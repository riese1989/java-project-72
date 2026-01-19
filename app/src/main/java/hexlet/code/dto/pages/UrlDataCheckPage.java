package hexlet.code.dto.pages;

import hexlet.code.dto.urls.UrlCheckDto;
import hexlet.code.utils.MessageRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class UrlDataCheckPage {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private List<UrlCheckDto> checks;
    private MessageRecord messageRecord;
}
