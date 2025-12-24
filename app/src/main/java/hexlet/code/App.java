package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controllers.UrlCheckController;
import hexlet.code.controllers.UrlController;
import hexlet.code.repositories.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static gg.jte.ContentType.Html;

@Slf4j
public final class App {

    public static void main(final String[] args) throws SQLException, IOException {
        var app = getApp();

        app.start(getPort());
    }

    public static Javalin getApp() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        var settingsMap = System.getenv();

        var templateUrl = settingsMap.get("JDBC_DATABASE_URL");

        if (templateUrl == null) {
            hikariConfig.setJdbcUrl("jdbc:h2:mem:project");
            hikariConfig.setDriverClassName("org.h2.Driver");
        } else {
            hikariConfig.setJdbcUrl(
                    StringSubstitutor.replace(templateUrl, settingsMap)
            );
            hikariConfig.setDriverClassName("org.postgresql.Driver");
        }

        var dataSource = new HikariDataSource(hikariConfig);
        var sql = readResourceFile("sql/schema.sql");

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

        app.get("/", ctx -> ctx.render("index.jte"));
        app.post(NamedRoutes.urlsPath(), UrlController::create);
        app.get(NamedRoutes.urlsPath(), UrlController::showAll);
        app.get(NamedRoutes.urlDataPath(), UrlCheckController::show);

        return app;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");

        return Integer.parseInt(port);
    }

    private static String readResourceFile(final String fileName)
            throws IOException {
        var inputStream = App.class.getClassLoader()
                .getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static TemplateEngine createTemplateEngine() {
        var classLoader = App.class.getClassLoader();
        var codeResolver = new ResourceCodeResolver("templates", classLoader);
        var templateEngine = TemplateEngine.create(codeResolver, Html);

        return templateEngine;
    }
}
