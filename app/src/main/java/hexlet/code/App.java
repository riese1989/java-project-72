package hexlet.code;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] args) {
        var app = getApp();

        app.start();
    }

    public static Javalin getApp() {

        return Javalin.create()
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);
    }
}
