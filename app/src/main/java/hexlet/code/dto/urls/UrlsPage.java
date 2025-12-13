package hexlet.code.dto.urls;

import hexlet.code.models.MessageRecord;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UrlsPage {
    private MessageRecord messageRecord;
    private List<UrlData> data;
}
