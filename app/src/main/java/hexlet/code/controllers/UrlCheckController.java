package hexlet.code.controllers;

import hexlet.code.dto.urls.UrlCheckDto;
import hexlet.code.dto.pages.UrlDataCheckPage;
import hexlet.code.utils.MessageRecord;
import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class UrlCheckController {

    public static void show(final Context ctx) throws SQLException {
        var urlData = getUrl(ctx);
        var pageBuilder = getPage(urlData, ctx);

        ctx.render("urlChecks.jte", model("page", pageBuilder));
    }

    public static void check(final Context ctx) throws SQLException {
        var urlId = Long.valueOf(ctx.pathParam("id"));
        MessageRecord messageRecord = null;

        var checkedUrlData = getUrl(ctx);

        try {
            var response = Unirest.get(checkedUrlData.getName()).asString();
            var builder = UrlCheck.builder()
                    .statusCode(response.getStatus())
                    .urlId(urlId)
                    .createdAt(LocalDateTime.now());

            if (response.getStatus() == HttpStatus.OK.getCode()) {
                var html = response.getBody();
                var doc = Jsoup.parse(html);

                Optional.of(doc.title()).ifPresent(builder::title);
                Optional.ofNullable(doc.selectFirst("h1")).ifPresent(h1 -> builder.h1(h1.text()));
                Optional.ofNullable(doc.selectFirst("meta[name=description]"))
                        .ifPresent(meta -> builder.description(meta.attr("content")));
            }

            UrlCheckRepository.save(builder.build());
        } catch (UnirestException e) {
            log.error(e.getMessage());

            messageRecord = MessageRecord.SERVICE_REQUEST_ERROR;
        } catch (SQLException e) {
            log.error(e.getMessage());

            messageRecord = MessageRecord.BAD_REQUEST_ERROR;
        } catch (Exception e) {
            log.error(e.getMessage());

            messageRecord = MessageRecord.UNKNOWN_ERROR;
        }

        var page = getPage(checkedUrlData, ctx);

        if (messageRecord != null) {
            page.setMessageRecord(messageRecord);
        }

        ctx.render("urlChecks.jte", model("page", page));
    }

    private static Url getUrl(final Context ctx) throws SQLException {
        var id = Long.valueOf(ctx.pathParam("id"));

        return UrlRepository.getById(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = %s not found".formatted(id)));

    }

    private static UrlDataCheckPage getPage(Url urlData, Context ctx) throws SQLException {
        var pageBuilder = UrlDataCheckPage.builder()
                .id(urlData.getId())
                .name(urlData.getName())
                .createdAt(urlData.getCreatedAt());
        var id = Long.valueOf(ctx.pathParam("id"));

        var urlChecks = UrlCheckRepository.getEntities(id);

        if (!urlChecks.isEmpty()) {
            var urlChecksData = new java.util.ArrayList<>(urlChecks.stream()
                    .filter(urlCheck -> urlCheck.getUrlId().equals(urlData.getId()))
                    .map(urlCheck ->
                            UrlCheckDto.builder()
                                    .id(urlCheck.getId())
                                    .codeAnswer(urlCheck.getStatusCode())
                                    .title(urlCheck.getTitle())
                                    .h1(urlCheck.getH1())
                                    .description(urlCheck.getDescription())
                                    .dateCheck(urlCheck.getCreatedAt())
                                    .build()
                    )
                    .toList());

            pageBuilder.checks(urlChecksData);
        }

        return pageBuilder.build();
    }
}
