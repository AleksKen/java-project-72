package hexlet.code.controller;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class ChecksController {

    public static void create(Context context) throws SQLException {
        var id = context.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id);
        try {
            var jsonResponse = Unirest.get(url.get().getName()).asString();
            var code = jsonResponse.getStatus();
            var body = jsonResponse.getBody();

            Document doc = Jsoup.parse(body);
            String title = doc.title();
            var elH1 = doc.select("h1").first();
            String h1 = null;
            if (elH1 != null) {
                h1 = elH1.text();
            }
            String description = doc.select("meta[name=description]").attr("content");

            Date currentDate = new Date();
            Timestamp timestamp = new Timestamp(currentDate.getTime());
            var urlCheck = new UrlCheck(code, title, h1, description, id, timestamp);
            UrlCheckRepository.save(urlCheck);
            context.sessionAttribute("flash", "Страница успешно проверена");
            context.sessionAttribute("flash-type", "success");
            context.redirect(NamedRoutes.urlPath(id));
        } catch (UnirestException | RuntimeException e) {
            context.sessionAttribute("flash", "Некорректный адрес");
            context.sessionAttribute("flash-type", "danger");
            context.redirect(NamedRoutes.urlPath(id));
        }
    }
}
