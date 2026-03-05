package server;

import java.util.Map;

import dataaccess.DatabaseManager;
import dataaccess.auth.AuthDAO;
import dataaccess.auth.SqlAuthDAO;
import dataaccess.game.GameDAO;
import dataaccess.game.SqlGameDAO;
import dataaccess.user.SqlUserDAO;
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
import response.ListResult;
import response.LoginResult;
import response.NewGameResult;
import response.RegisterResult;
import services.GameService;
import services.UserService;
import websocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;
    private final DatabaseManager databaseManager = new DatabaseManager();
    private final GameDAO gameDAO = new SqlGameDAO(); 
    private final UserDAO userDAO = new SqlUserDAO();
    private final AuthDAO authDAO = new SqlAuthDAO();

    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final UserService userService = new UserService(userDAO, authDAO);

    public Server() {
        javalin = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        javalin.before(ctx -> System.out.println("Inbound Request: " + ctx.method() + " " + ctx.path()));

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

        // websocket connection

        WebSocketHandler wsHandler = new WebSocketHandler(authDAO);
        javalin.ws("/ws", ws -> {
            ws.onConnect(wsHandler::handleConnect);
            ws.onMessage(wsHandler::handleMessage);
            ws.onClose(wsHandler::handleClose);
            ws.onError(ctx -> System.out.println("WS Error: " + ctx.error().getMessage()));
        });
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

        if (req.gameID() == null) {
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
        System.out.println("DEBUG: Received request body: " + ctx.body()); // See the raw JSON
        RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
        
        if (req.username() == null || req.password() == null || req.email() == null ||
        req.username().isEmpty() || req.password().isEmpty() || req.email().isEmpty()) {
            System.out.println("DEBUG: Validation failed, throwing BadRequest");
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
