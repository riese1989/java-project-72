package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repositories.BaseRepository;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

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
        var sql = readResourceFile("schema.sql");

        log.info(sql);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.setDataSource(dataSource);

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        app.get("/", ctx -> ctx.result("Hello World"));

        return app;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");

        return Integer.parseInt(port);
    }
    
    private static String getDbUrl() {
        var settingsMap = System.getenv();
        var baseUrl = settingsMap.get("JDBC_DATABASE_URL");

        if (baseUrl == null) {
            return "jdbc:h2:mem:project";
        }

        return baseUrl.replaceAll("\\{DATABASE}", settingsMap.get("DATABASE"))
                .replaceAll("\\{DB_PORT}", settingsMap.get("DB_PORT"))
                .replaceAll("\\{HOST}", settingsMap.get("HOST"))
                .replaceAll("\\{PASSWOD}", settingsMap.get("PASSWORD"))
                .replaceAll("\\{USERNAME}", settingsMap.get("USERNAME"));
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
