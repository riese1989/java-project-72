package hexlet.code.controllers;

import hexlet.code.dto.urls.UrlCheckDto;
import hexlet.code.dto.urls.UrlDataCheckPage;
import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static io.javalin.rendering.template.TemplateUtil.model;
import static java.util.Optional.ofNullable;

public class UrlCheckController {

    public static void show(final Context ctx) throws SQLException {
        var urlData = getUrl(ctx);

        drowPage(urlData, ctx);
    }

    public static void check(final Context ctx) throws SQLException {
        var urlId = Long.valueOf(ctx.pathParam("id"));
        var checkedUrlData = getUrl(ctx);
        var response = Unirest.get(checkedUrlData.getName()).asString();
        var builder = UrlCheck.builder()
                .statusCode(response.getStatus())
                .urlId(urlId)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()));

        if (response.getStatus() == HttpStatus.OK.getCode()) {
            var html = response.getBody();
            var doc = Jsoup.parse(html);

            Optional.of(doc.title()).ifPresent(builder::title);
            ofNullable(doc.selectFirst("h1")).ifPresent(h1 -> builder.h1(h1.text()));
            ofNullable(doc.selectFirst("meta[name=description]"))
                    .ifPresent(meta -> builder.description(meta.attr("content")));
        }

        UrlCheckRepository.save(builder.build());
        drowPage(checkedUrlData, ctx);
    }

    private static Url getUrl(final Context ctx) throws SQLException {
        var id = Long.valueOf(ctx.pathParam("id"));

        return UrlRepository.getById(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = %s not found".formatted(id)));

    }

    private static void drowPage(Url urlData, Context ctx) throws SQLException {
        var page = UrlDataCheckPage.builder()
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

            page.checks(urlChecksData);
        }

        ctx.render("urlChecks.jte", model("page", page.build()));
    }
}
