package hexlet.code.controllers;

import hexlet.code.dto.urls.UrlData;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.models.MessageRecord;
import hexlet.code.models.URL;
import hexlet.code.repositories.UrlRepository;
import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static io.javalin.rendering.template.TemplateUtil.model;

public final class UrlController {
    public static void create(Context ctx) throws MalformedURLException, URISyntaxException {
        var inputUrl = ctx.formParam("url");
        var domainWithProtocolAndPort = extractDomainWithProtocolAndPort(inputUrl);
        var url = new URL(domainWithProtocolAndPort);
        MessageRecord messageRecord;

        try {
            UrlRepository.save(url);

            messageRecord = MessageRecord.OK;
        }
        catch (Exception ex) {
            messageRecord = MessageRecord.NOT;
        }

        List<UrlData> urlDataList = UrlRepository.showAll().stream()
                .map(urlData -> new UrlData(
                        urlData.getId().intValue(),
                        urlData.getName(),
                        Timestamp.valueOf(LocalDateTime.now()),
                        200 // Пример кода ответа, можно заменить на реальное значение
                ))
                .toList();

        ctx.render("urls.jte", model("page", UrlsPage.builder().messageRecord(messageRecord).data(urlDataList).build()));
    }

    public static void showAll(Context ctx) {
        List<UrlData> urlDataList = UrlRepository.showAll().stream()
                .map(urlData -> new UrlData(
                        urlData.getId().intValue(),
                        urlData.getName(),
                        urlData.getCreatedAt(),
                        200 // Пример кода ответа, можно заменить на реальное значение
                ))
                .toList();

        ctx.render("urls.jte", model("page", UrlsPage.builder().data(urlDataList).build()));
    }

    private static String extractDomainWithProtocolAndPort(String inputUrl) throws URISyntaxException, MalformedURLException {
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
