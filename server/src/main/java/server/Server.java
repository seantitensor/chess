package server;

import java.util.Map;

import dataaccess.auth.AuthDAO;
import dataaccess.auth.LocalAuthDAO;
import dataaccess.game.GameDAO;
import dataaccess.game.LocalGameDAO;
import dataaccess.user.LocalUserDAO;
import dataaccess.user.UserDAO;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResult;
import response.NewGameResult;
import response.RegisterResult;
import response.ListResult;
import services.GameService;
import services.UserService;

public class Server {

    private final Javalin javalin;
    private final GameDAO gameDAO = new LocalGameDAO(); 
    private final UserDAO userDAO = new LocalUserDAO();
    private final AuthDAO authDAO = new LocalAuthDAO();

    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final UserService userService = new UserService(userDAO, authDAO);

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
    private void clear(Context ctx) throws Exception {
        userService.clearDB();
        gameService.clearDB();
        ctx.status(200).json(Map.of());
    }

    //game handler
    private void listGames(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        ListResult result = gameService.getListGames(authToken);
        ctx.status(200).json(result);
    }

    private void createGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        CreateGameRequest req = ctx.bodyAsClass(CreateGameRequest.class);
        if (req.gameName() == null) {
            throw new BadRequestException("bad request");
        }

        NewGameResult result = gameService.createGame(authToken, req.gameName());
        ctx.status(200).json(result);
    }

    private void joinGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        JoinGameRequest req = ctx.bodyAsClass(JoinGameRequest.class);

        if (req.playerColor() == null || req.gameID() == null) {
            throw new BadRequestException("bad request");
        }

        gameService.joinGame(authToken,req);
        ctx.status(200).json(Map.of());
    }

    //user handler
    private void login(Context ctx) throws Exception {
        LoginRequest req = ctx.bodyAsClass(LoginRequest.class);

        if (req.username() == null || req.password() == null) {
            throw new BadRequestException("bad request");
        }

        LoginResult result = userService.login(req);
        ctx.status(200).json(result);
    }

    private void register(Context ctx) throws Exception {
        RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
        
        if (req.username() == null || req.password() == null || req.email() == null ||
        req.username().isEmpty() || req.password().isEmpty() || req.email().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        RegisterResult result = userService.register(req);
        ctx.status(200).json(result);
    }

    private void logout(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);        
        ctx.status(200).json(Map.of());
    }
}
