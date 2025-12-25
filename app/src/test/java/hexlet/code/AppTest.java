package hexlet.code;

import hexlet.code.models.MessageRecord;
import hexlet.code.models.Url;
import hexlet.code.repositories.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    private Javalin app;
    private final Timestamp date = Timestamp.valueOf("2023-01-01 00:00:00");
    private final MockWebServer server = new MockWebServer();

    @BeforeEach
    public final void setUp() throws SQLException, IOException {
        app = App.getApp();
        UrlRepository.truncate();
        server.start();
    }

    @AfterEach
    public final void tearDown() throws IOException {
       server.shutdown();
    }

    @Test
    @DisplayName("Тестируем главную страницу")
    public void mainPageTest() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Введите URL:");
        });
    }

    @Test
    @DisplayName("Добавление нового URL, когда таблица пустая")
    public void addUrlEmptyDbTest() {
        var requestBody = "url=https://gitverse.ru/features/gigacode/install/";

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), requestBody);

            assertThat(response.code()).isEqualTo(200);

            var body = response.body().string();

            assertThat(body).contains("<td>1</td>");
            assertThat(body).contains("<td><a href=\"/urls/1\">https://gitverse.ru</a></td>");
            assertThat(body).contains(MessageRecord.OK.getMessage());
        });

        var urls = UrlRepository.getData();

        assertEquals(1, urls.size());

        assertEquals(1, urls.get(0).getId());
        assertEquals("https://gitverse.ru", urls.get(0).getName());
    }

    @Test
    @DisplayName("Добавление нового URL, когда таблица не пустая")
    public void addUrlNonEmptyDbTest() throws SQLException {
        var url = Url.builder().name("https://habr.com").createdAt(date).build();

        UrlRepository.save(url);

        var requestBody = "url=https://gitverse.ru/features/gigacode/install/";

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), requestBody);

            assertThat(response.code()).isEqualTo(200);

            var body = response.body().string();

            assertThat(body).contains("<td>1</td>");
            assertThat(body).contains("<td><a href=\"/urls/1\">https://habr.com</a></td>");
            assertThat(body).contains("<td>2</td>");
            assertThat(body).contains("<td><a href=\"/urls/2\">https://gitverse.ru</a></td>");
            assertThat(body).contains(MessageRecord.OK.getMessage());
        });

        var urls = UrlRepository.getData();

        assertEquals(2, urls.size());

        assertEquals(1, urls.get(0).getId());
        assertEquals("https://habr.com", urls.get(0).getName());
        assertEquals(2, urls.get(1).getId());
        assertEquals("https://gitverse.ru", urls.get(1).getName());
    }

    @Test
    @DisplayName("Добавление нового URL, когда таблица не пустая")
    public void addUrlErrorDbTest() throws SQLException {
        var url = Url.builder().name("https://gitverse.ru").createdAt(date).build();

        UrlRepository.save(url);

        var requestBody = "url=https://gitverse.ru/features/gigacode/install/";

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), requestBody);

            assertThat(response.code()).isEqualTo(200);

            var body = response.body().string();

            assertThat(body).contains("<td>1</td>");
            assertThat(body).contains("<td><a href=\"/urls/1\">https://gitverse.ru</a></td>");
            assertThat(body).contains(MessageRecord.PAGE_EXISTS.getMessage());
        });

        var urls = UrlRepository.getData();

        assertEquals(1, urls.size());

        assertEquals(1, urls.get(0).getId());
        assertEquals("https://gitverse.ru", urls.get(0).getName());
    }

    @Test
    @DisplayName("Показываем список урлов, когда таблица пустая")
    public void showUrlsEmptyDbTest() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());

            assertThat(response.code()).isEqualTo(200);

            var body = response.body().string();

            assertThat(body).doesNotContain("<td></td>");
        });

        var urls = UrlRepository.getData();

        assertEquals(0, urls.size());
    }

    @Test
    @DisplayName("Когда переходим на страницу urlChecks, но используем несуществующий urlId")
    public void showUrlChecksErrorTest() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.checkUrlPath(1L));

            assertThat(response.code()).isEqualTo(404);
        });
    }
}