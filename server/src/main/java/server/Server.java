package server;

import java.util.Map;

import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import request.RegisterRequest;
import response.RegisterResult;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            // Gson gson = new GsonBuilder().create();
            config.jsonMapper(new JavalinGson());
        });

        // Register your endpoints and exception handlers here.

        // user endpoints
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);

        // game endpoints
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);

        // clear endpoints
        javalin.delete("/db", this::clear);

        // exception handling
        javalin.exception(Exception.class, this::exceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    // exception handler
    private void exceptionHandler(Exception e, Context context) {
        var body = Map.of("message", String.format("Error: %s", e.getMessage()), "success", false);
        int status = 500;
        if (e instanceof BadRequestException) {
            status = 400;
        } else if (e instanceof UnauthorizedException) {
            status = 401;
        } else if (e instanceof AlreadyTakenException) {
            status = 403;
        }
        context.status(status);
        context.json(body);
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

    private void register(Context ctx) throws Exception {
        RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
        
        if (req.username() == null || req.password() == null || req.email() == null ||
        req.username().isEmpty() || req.password().isEmpty() || req.email().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        if (isUsernameInDatabase(req.username())) {
            throw new AlreadyTakenException("already taken");
        }

        String mockAuthToken = java.util.UUID.randomUUID().toString();
        RegisterResult result = new RegisterResult(req.username(), mockAuthToken);

        ctx.status(200).json(result);
    }

    private boolean isUsernameInDatabase(String username) {
        return "Sean".equals(username);
    }

    private void logout(Context ctx) {
        ctx.status(200).result("Logout succesful");
    }
}
