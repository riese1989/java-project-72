package hexlet.code.dto.pages;

import hexlet.code.dto.urls.UrlDataDto;
import hexlet.code.utils.MessageRecord;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UrlsPage {
    private MessageRecord messageRecord;
    private List<UrlDataDto> data;
}
