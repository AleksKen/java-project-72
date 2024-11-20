package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void build(Context context) {
        String flash = context.consumeSessionAttribute("flash");
        String flashType = context.consumeSessionAttribute("flash-type");
        var page = new MainPage();
        page.setFlash(flash);
        page.setFlashType(flashType);
        context.render("index.jte", model("page", page));
    }

    public static void index(Context context) throws SQLException {
        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls);
        String flash = context.consumeSessionAttribute("flash");
        String flashType = context.consumeSessionAttribute("flash-type");
        page.setFlash(flash);
        page.setFlashType(flashType);
        context.render("urls/index.jte", model("page", page));
    }


    public static void show(Context context) throws SQLException {
        var id = context.pathParamAsClass("id", Long.class).getOrDefault(-1L);
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
        String flash = context.consumeSessionAttribute("flash");
        String flashType = context.consumeSessionAttribute("flash-type");
        var page = new UrlPage(url);
        page.setFlash(flash);
        page.setFlashType(flashType);
        context.render("urls/show.jte", model("page", page));
    }

    public static void create(Context context) {
        var urlString = context.formParamAsClass("url", String.class).get();
        try {
            var url = new URI(urlString).toURL();
            var protocol = url.getProtocol();
            var host = url.getHost();
            var port = url.getPort();
            var name = "";
            if (port == -1) {
                name = String.format("%s://%s", protocol, host);
            } else {
                name = String.format("%s://%s:%d", protocol, host, port);
            }
            if (UrlRepository.search(name).isEmpty()) {
                Date currentDate = new Date();
                Timestamp timestamp = new Timestamp(currentDate.getTime());
                UrlRepository.save(new Url(name, timestamp));
            } else {
                throw new SQLException("Страница уже существует");
            }
            context.sessionAttribute("flash", "Страница успешно добавлена");
            context.sessionAttribute("flash-type", "success");
            context.redirect(NamedRoutes.urlsPath());
        } catch (SQLException e) {
            context.sessionAttribute("flash", e.getMessage());
            context.sessionAttribute("flash-type", "info");
            context.redirect(NamedRoutes.urlsPath());
        } catch (Exception e) {
            context.sessionAttribute("flash", "Некорректный URL");
            context.sessionAttribute("flash-type", "danger");
            context.redirect(NamedRoutes.rootPath());
        }
    }
}
