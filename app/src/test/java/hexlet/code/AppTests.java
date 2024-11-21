package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class AppTests {
    private static Javalin app;
    private static MockWebServer mockWebServer;

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        app = App.getApp();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockWebServer.shutdown();
        app.stop();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com");
            response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com");
            response = client.get(NamedRoutes.urlPath(1L));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com");
        });
    }

    @Test
    public void testUrlRepository() throws SQLException {
        var url = new Url("https://www.example.com", new Timestamp(new Date().getTime()));
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(UrlRepository.search(url.getName()).get().getId()));
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlCheck() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            client.post(NamedRoutes.urlsPath(), requestBody);

            var urlsResponse = client.get(NamedRoutes.urlsPath());
            assertThat(urlsResponse.code()).isEqualTo(200);

            String htmlContent = new String(Files.readAllBytes(
                    Paths.get("src/test/resources/hexlet/code/example.html")));

            mockWebServer.enqueue(new MockResponse().setBody(htmlContent).setResponseCode(200));

            var urlCheckResponse = client.post(NamedRoutes.checksPath(1L));
            assertThat(urlCheckResponse.code()).isEqualTo(200);

            var urlPageResponse = client.get(NamedRoutes.urlPath(1L));
            assertThat(urlPageResponse.code()).isEqualTo(200);
            assertThat(urlPageResponse.body().string()).contains("Example Domain");
        });
    }

}
