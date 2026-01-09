package hexlet.code.controllers;

import hexlet.code.dto.urls.UrlCheckDto;
import hexlet.code.dto.urls.UrlDataDto;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.models.MessageRecord;
import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.javalin.rendering.template.TemplateUtil.model;

public final class UrlController {
    private static final UrlRepository repo = new UrlRepository();

    public static void create(final Context ctx)
            throws MalformedURLException, URISyntaxException {
        var inputUrl = ctx.formParam("url");
        var domainWithProtocolAndPort = getShortenUrl(inputUrl);
        var url = Url.builder().name(domainWithProtocolAndPort)
                .createdAt(Timestamp.valueOf(LocalDateTime.now())).build();

        MessageRecord messageRecord;

        try {
            repo.save(url);

            messageRecord = MessageRecord.OK;
        }
        catch (Exception ex) {
            messageRecord = MessageRecord.PAGE_EXISTS;
        }

        List<UrlDataDto> urlDataList = UrlRepository.getData().stream()
                .map(urlData -> UrlDataDto.builder()
                        .id(urlData.getId())
                        .nameUrl(urlData.getName())
                        .urlCheckDto(
                                UrlCheckRepository.getData().stream()
                                        .filter(check -> Objects.equals(check.getUrlId(), urlData.getId()))
                                        .max(Comparator.comparing(UrlCheck::getCreatedAt))
                                        .map(check -> UrlCheckDto.builder()
                                                    .codeAnswer(check.getStatusCode())
                                                    .dateCheck(check.getCreatedAt())
                                                    .build())
                                        .orElse(null)
                        )
                        .build()
                )
                .toList();

        ctx.render("urls.jte",
                model("page",
                        UrlsPage.builder()
                                .messageRecord(messageRecord)
                                .data(urlDataList)
                                .build()));
    }

    public static void showAll(final Context ctx) {
        var latestChecks = UrlCheckRepository.getData().stream()
                .collect(Collectors.toMap(
                        UrlCheck::getUrlId,      // Ключ — urlId
                        check -> check,          // Значение — сам объект
                        (existing, replacement) ->
                                existing.getCreatedAt().after(replacement.getCreatedAt()) ? existing : replacement
                ))                           // На выходе Map<Long, UrlCheck>
                .values()                    // Берем только значения (самые свежие UrlCheck)
                .stream()
                .toList();

        List<UrlDataDto> urlDataDtoList = UrlRepository.getData().stream()
                .map(urlData -> UrlDataDto.builder()
                        .id(urlData.getId())
                        .nameUrl(urlData.getName())
                        .urlCheckDto(
                                latestChecks.stream()
                                        .filter(check -> check.getUrlId() == urlData.getId())
                                        .findFirst()
                                        .map(urlCheck -> UrlCheckDto.builder()
                                                .codeAnswer(urlCheck.getStatusCode())
                                                .dateCheck(urlCheck.getCreatedAt())
                                                .build())
                                        .orElse(null))
                        .build()
                )
                .toList();

        ctx.render("urls.jte", model("page",
                UrlsPage.builder()
                        .data(urlDataDtoList)
                        .build()));
    }

    private static String getShortenUrl(final String inputUrl)
            throws URISyntaxException, MalformedURLException {
        var uri = new URI(inputUrl);
        var url = uri.toURL();
        var protocol = url.getProtocol();
        var host = url.getHost();
        int port = url.getPort();

        if (port == -1) {
            return protocol + "://" + host;
        }

        return protocol + "://" + host + ":" + port;
    }
}
