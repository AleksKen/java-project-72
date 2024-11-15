package hexlet.code;

import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

public class App {
    public static void main(String[] args) {
        var app = getApp();
        app.start(getPort());
    }

    public static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    public static Javalin getApp() {
        var app = Javalin.create(javalinConfig -> {
            javalinConfig.bundledPlugins.enableDevLogging();
            javalinConfig.fileRenderer(new JavalinJte());
        });

//        app.before(ctx -> {
//            ctx.contentType("text/html; charset=utf-8");
//        });

        app.get(NamedRoutes.rootPath(), context -> context.result("Hello World"));
        return app;
    }
}

