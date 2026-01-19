package hexlet.code.controllers;

import hexlet.code.dto.urls.UrlDataDto;
import hexlet.code.dto.pages.UrlsPage;
import hexlet.code.utils.MessageRecord;
import hexlet.code.models.Url;
import hexlet.code.repositories.UrlDataRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static io.javalin.rendering.template.TemplateUtil.model;

public final class UrlController {

    public static void create(final Context ctx)
            throws MalformedURLException, URISyntaxException, SQLException {
        var inputUrl = ctx.formParam("url");
        var domainWithProtocolAndPort = getShortenUrl(inputUrl);
        var url = Url.builder().name(domainWithProtocolAndPort)
                .createdAt(LocalDateTime.now()).build();

        MessageRecord messageRecord;

        try {
            UrlRepository.save(url);

            messageRecord = MessageRecord.OK;
        } catch (Exception ex) {
            messageRecord = MessageRecord.PAGE_EXISTS_ERROR;
        }


        var urlDataList = prepareUrlData();
        var page = UrlsPage.builder()
                .messageRecord(messageRecord)
                .data(urlDataList)
                .build();

        ctx.render("urls.jte", model("page", page));
    }

    public static void showAll(final Context ctx) throws SQLException {
        var urlDataList = prepareUrlData();
        var page = UrlsPage.builder()
                .data(urlDataList)
                .build();

        ctx.render("urls.jte", model("page", page));
    }

    private static List<UrlDataDto> prepareUrlData() throws SQLException {
        return UrlDataRepository.getUrlData().stream()
                .map(urlData -> UrlDataDto.builder()
                        .id(urlData.getId())
                        .nameUrl(urlData.getName())
                        .codeAnswer(urlData.getCodeAnswer())
                        .dateLastCheck(urlData.getDateLastCheck())
                        .build()
                )
                .toList();
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
