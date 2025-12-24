package hexlet.code.controllers;

import hexlet.code.dto.urls.UrlCheckDto;
import hexlet.code.dto.urls.UrlDataCheckPage;
import hexlet.code.models.Url;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.http.Context;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlCheckController {

    public static void show(final Context ctx) {
        var urls = UrlRepository.getData();
        var urlChecks = UrlCheckRepository.getData();

        var urlId = Long.valueOf(ctx.pathParam("id"));

        var urlData = urls.stream().filter(url -> url.getId().equals(urlId)).findFirst().orElse(Url.builder().build());
        var page = UrlDataCheckPage.builder()
                .id(urlId)
                .name(urlData.getName())
                .createdAt(urlData.getCreatedAt());

        if (!urlChecks.isEmpty()) {
            var urlChecksData = new java.util.ArrayList<>(urlChecks.stream()
                    .filter(urlCheck -> urlCheck.getUrlId().equals(urlId))
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
