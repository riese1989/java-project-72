package hexlet.code;

import hexlet.code.models.MessageRecord;
import hexlet.code.models.Url;
import hexlet.code.repositories.BaseRepository;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    private Javalin app;
    private final LocalDateTime date = LocalDateTime.parse("2023-01-01T00:00:00");

    @BeforeEach
    public final void setUp() throws SQLException, IOException {
        app = App.getApp();

        try (var conn = BaseRepository.getDataSource().getConnection();
             var preparedStatement = conn.createStatement()) {
            preparedStatement.execute("DELETE FROM urls");
            preparedStatement.execute("DELETE FROM url_checks");
        }
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
    public void addUrlEmptyDbTest() throws SQLException {
        var requestBody = "url=https://gitverse.ru/features/gigacode/install/";

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), requestBody);

            assertThat(response.code()).isEqualTo(200);
            assertNotNull(response.body());

            var body = response.body().string();

            assertThat(body).contains("<td>1</td>");
            assertThat(body).contains("<td><a href=\"/urls/1\">https://gitverse.ru</a></td>");
            assertThat(body).contains(MessageRecord.OK.getMessage());
        });

        var url = UrlRepository.getById(1L).get();

        assertEquals(1, url.getId());
        assertEquals("https://gitverse.ru", url.getName());
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

        assertEquals(1L, url.getId());

        var url1 = UrlRepository.getById(1L).get();

        assertEquals(1, url1.getId());
        assertEquals("https://habr.com", url1.getName());

        var url2 = UrlRepository.getById(2L).get();

        assertEquals(2, url2.getId());
        assertEquals("https://gitverse.ru", url2.getName());
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
            assertNotNull(response.body());

            var body = response.body().string();

            assertThat(body).contains("<td>1</td>");
            assertThat(body).contains("<td><a href=\"/urls/1\">https://gitverse.ru</a></td>");
            assertThat(body).contains(MessageRecord.PAGE_EXISTS.getMessage());
        });

        assertEquals(1L, url.getId());

        var urlData = UrlRepository.getById(1L).get();

        assertEquals(1, urlData.getId());
        assertEquals("https://gitverse.ru", urlData.getName());
    }

    @Test
    @DisplayName("Показываем список урлов, когда таблица пустая")
    public void showUrlsEmptyDbTest() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());

            assertThat(response.code()).isEqualTo(200);

            var body = response.body().string();

            assertThat(body).doesNotContain("<td></td>");
        });

        var url = UrlRepository.getById(1L).orElse(null);

        assertNull(url);
    }

    @Test
    @DisplayName("Когда переходим на страницу urlChecks, но используем несуществующий urlId")
    public void showUrlChecksErrorTest() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlDataPath(1L));

            assertThat(response.code()).isEqualTo(404);
            assertNotNull(response.body());
            assertThat(response.body().string()).contains("Url with id = 1 not found");
        });
    }

    @Test
    @DisplayName("Запускаем несколько раз проверку url")
    public void showUrlChecksTest() throws SQLException, IOException {
        var mockWebServer = new MockWebServer();
        var id = 1L;

        var htmlMissing = "<html>"
                + "<head><title>Только заголовок</title></head>"
                + "<body></body>"
                + "</html>";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(htmlMissing));

        var htmlFull = "<html>"
                + "<head>"
                + "  <title>Заголовок страницы</title>"
                + "  <meta name=\"description\" content=\"Описание сайта для SEO\">"
                + "</head>"
                + "<body>"
                + "  <h1>Основной заголовок H1</h1>"
                + "</body>"
                + "</html>";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(htmlFull));

        mockWebServer.start();

        var urlString = mockWebServer.url("/").toString();
        var requestBody = "id=" + id;


        var url = Url.builder().name(urlString).createdAt(date).build();

        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            client.post(NamedRoutes.checkUrlPath(id), requestBody);
            client.post(NamedRoutes.checkUrlPath(id), requestBody);

            var responseChecks = client.get(NamedRoutes.urlDataPath(id));

            assertThat(responseChecks.code()).isEqualTo(200);
            assertNotNull(responseChecks.body());

            var bodyChecks = responseChecks.body().string();

            assertNotNull(bodyChecks);
            assertThat(bodyChecks).contains("<td>1</td>");
            assertThat(bodyChecks).contains("<td>404</td>");
            assertThat(bodyChecks).contains("<td></td>");
            assertThat(bodyChecks).contains("<td>2</td>");
            assertThat(bodyChecks).contains("<td>200</td>");
            assertThat(bodyChecks).contains("<td>Заголовок страницы</td>");
            assertThat(bodyChecks).contains("<td>Основной заголовок H1</td>");
            assertThat(bodyChecks).contains("<td>Описание сайта для SEO</td>");

            var responseUrls = client.get(NamedRoutes.urlsPath());

            assertNotNull(responseUrls.body());

            var bodyUrls = responseUrls.body().string();

            assertThat(bodyUrls).contains("<td>1</td>");
            assertThat(bodyUrls).contains("<td><a href=\"/urls/1\">%s</a></td>".formatted(urlString));
            assertThat(bodyUrls).contains("<td>200</td>");
            }
        );

        var urlId = url.getId();

        assertEquals(1L, urlId);

        var urlChecks = UrlCheckRepository.getEntities(urlId);

        assertNotNull(urlChecks);
        assertEquals(2, urlChecks.size());

        assertTrue(urlChecks.get(0).toString()
                .contains("id=1, statusCode=404, title=null, h1=null, description=null, urlId=1"));
        assertTrue(urlChecks.get(1).toString()
                .contains("id=2, statusCode=200, title=Заголовок страницы, h1=Основной заголовок H1, "
                        + "description=Описание сайта для SEO, urlId=1"));

        var dbUrl = UrlRepository.getById(id);

        assertNotNull(dbUrl);

        assertTrue(dbUrl.toString()
                .contains("id=1, name=http"));
        mockWebServer.shutdown();
    }
}
