package server;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        // user endpoints
        javalin.post("/user", this::register);
        javalin.post("/user", this::login);
        javalin.delete("/user", this::logout);

        // game endpoints
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);

        // clear endpoints
        javalin.delete("/db", this::clear);

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

    // clear handler
    private void clear(Context ctx) {
        ctx.status(200).json("{\"message\": cleared application succesfully}");
    }

    //game handler
    private void listGames(Context ctx) {
        ctx.status(200).json("{\"message\": found games}");
    }

    private void createGame(Context ctx) {
        ctx.status(200).result("game Created");
    }

    private void joinGame(Context ctx) {
        ctx.status(200).result("game joined sucesfully");
    }

    //user handler
    private void login(Context ctx) {
        ctx.status(200).json("{\"message\": Login successful}");
    }

    private void register(Context ctx) {
        ctx.status(200).result("User created");
    }

    private void logout(Context ctx) {
        ctx.status(200).result("Logout succesful");
    }
}
