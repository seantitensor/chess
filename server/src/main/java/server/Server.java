package server;

import handlers.ClearHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import io.javalin.Javalin;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        UserHandler userHandler = new UserHandler();
        GameHandler gameHandler = new GameHandler();
        ClearHandler clearHandler = new ClearHandler();

        // user endpoints
        javalin.post("/user",userHandler::register);
        javalin.post("/user",userHandler::login);
        javalin.delete("/user", userHandler::logout);

        // game endpoints
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);

        // clear endpoints
        javalin.delete("/db", clearHandler::clear);

        // exception handling
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json("{\"message\": \"Error: " + e.getMessage() + "\"}");
            
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
