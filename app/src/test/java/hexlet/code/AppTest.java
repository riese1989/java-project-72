package hexlet.code;

import hexlet.code.models.MessageRecord;
import hexlet.code.models.URL;
import hexlet.code.repositories.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    private Javalin app;

    @BeforeEach
    public final void setUp() throws SQLException, IOException {
        app = App.getApp();
        UrlRepository.truncate();
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
            assertThat(body).contains("<td>https://gitverse.ru</td>");
            assertThat(body).contains(MessageRecord.OK.getMessage());
        });

        var urls = UrlRepository.showAll();

        assertEquals(1, urls.size());

        assertEquals(1, urls.get(0).getId());
        assertEquals("https://gitverse.ru", urls.get(0).getName());
    }

    @Test
    @DisplayName("Добавление нового URL, когда таблица не пустая")
    public void addUrlNonEmptyDbTest() throws SQLException {
        var url = new URL("https://habr.com");

        UrlRepository.save(url);

        var requestBody = "url=https://gitverse.ru/features/gigacode/install/";

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), requestBody);

            assertThat(response.code()).isEqualTo(200);

            var body = response.body().string();

            assertThat(body).contains("<td>1</td>");
            assertThat(body).contains("https://habr.com");
            assertThat(body).contains("<td>2</td>");
            assertThat(body).contains("https://gitverse.ru");
            assertThat(body).contains(MessageRecord.OK.getMessage());
        });

        var urls = UrlRepository.showAll();

        assertEquals(2, urls.size());

        assertEquals(1, urls.get(0).getId());
        assertEquals("https://habr.com", urls.get(0).getName());
        assertEquals(2, urls.get(1).getId());
        assertEquals("https://gitverse.ru", urls.get(1).getName());
    }

    @Test
    @DisplayName("Добавление нового URL, когда таблица не пустая")
    public void addUrlErrorDbTest() throws SQLException {
        var url = new URL("https://gitverse.ru");

        UrlRepository.save(url);

        var requestBody = "url=https://gitverse.ru/features/gigacode/install/";

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), requestBody);

            assertThat(response.code()).isEqualTo(200);

            var body = response.body().string();

            assertThat(body).contains("<td>1</td>");
            assertThat(body).contains("https://gitverse.ru");
            assertThat(body).contains(MessageRecord.PAGE_EXISTS.getMessage());
        });

        var urls = UrlRepository.showAll();

        assertEquals(1, urls.size());

        assertEquals(1, urls.get(0).getId());
        assertEquals("https://gitverse.ru", urls.get(0).getName());
    }
}