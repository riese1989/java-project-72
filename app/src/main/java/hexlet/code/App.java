package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controllers.UrlController;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.models.MessageRecord;
import hexlet.code.models.URL;
import hexlet.code.repositories.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class App {

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();

        app.start(getPort());
    }

    public static Javalin getApp() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        var dbUrl = getDbUrl();

        hikariConfig.setJdbcUrl(dbUrl);
        hikariConfig.setDriverClassName("org.postgresql.Driver");

        var dataSource = new HikariDataSource(hikariConfig);
        var sql = readResourceFile("sql/schema.sql");

        log.info(sql);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.setDataSource(dataSource);

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        app.get("/", ctx -> ctx.render("index.jte", model("page")));
        app.post(NamedRoutes.urlsPath(), ctx -> {
            var inputUrl = ctx.formParam("url");
            var domainWithProtocolAndPort = extractDomainWithProtocolAndPort(inputUrl);
            var url = new URL(domainWithProtocolAndPort);
            MessageRecord messageRecord;

            try {
                UrlController.create(url);
                messageRecord = MessageRecord.OK;
            }
            catch (Exception ex) {
                messageRecord = MessageRecord.NOT;
            }

            ctx.sessionAttribute("flash_message_id", messageRecord.getId());
            ctx.sessionAttribute("flash_message_text", messageRecord.getMessage());

            ctx.render("urls.jte", model("page", UrlsPage.builder().messageRecord(messageRecord)));
        });

        return app;
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

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");

        return Integer.parseInt(port);
    }

    private static String getDbUrl() {
        var settingsMap = System.getenv();
        var templateUrl = settingsMap.get("JDBC_DATABASE_URL");

        if (templateUrl == null) {
            return "jdbc:h2:mem:project";
        }

        return StringSubstitutor.replace(templateUrl, settingsMap);
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static TemplateEngine createTemplateEngine() {
        var classLoader = App.class.getClassLoader();
        var codeResolver = new ResourceCodeResolver("templates", classLoader);
        var templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);

        return templateEngine;
    }
}
