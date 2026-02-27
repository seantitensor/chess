package client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import exception.ResponseException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clear() throws Exception {;
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // user tests
    @Test
    public void postiveLoginTest() throws ResponseException {
        facade.register(new RegisterRequest("name", "pass", "email"));
        var result = facade.login(new LoginRequest("name", "pass"));
        assertNotNull(result.authToken());
    }

     @Test
    public void negativeLoginTest() throws ResponseException {
        assertThrows(ResponseException.class,
             () -> facade.login(new LoginRequest("bad", "badpass")));
    }

     @Test
    public void postiveRegisterTest() throws ResponseException {
        var result = facade.register(new RegisterRequest("name", "pass", "email"));
        assertNotNull(result.authToken());
    }

     @Test
    public void negativeRegisterTest() throws ResponseException {
        assertThrows(ResponseException.class,
            () -> facade.register(new RegisterRequest(null,"bad", "badpass")));
    }

     @Test
    public void postiveLogoutTest() throws ResponseException {
        var result = facade.register(new RegisterRequest("name", "pass", "email"));
        assertDoesNotThrow(() ->facade.logout(result.authToken()));
    }

     @Test
    public void negativeLogoutTest() throws ResponseException {
        assertThrows(ResponseException.class,
             () -> facade.logout("badAuthToken"));
    }

    // game tests
     @Test
    public void postiveCreateTest() throws ResponseException {
        var result = facade.register(new RegisterRequest("name", "pass", "email"));
        var game = facade.createGame(result.authToken(), new CreateGameRequest("gameName"));
        assertNotNull(game.gameID());
    }

     @Test
    public void negativeCreateTest() throws ResponseException {
        assertThrows(ResponseException.class,
             () -> facade.createGame(null, new CreateGameRequest("gameName")));
    }

     @Test
    public void postiveJoinTest() throws ResponseException {
        var result = facade.register(new RegisterRequest("name", "pass", "email"));
        var result2 = facade.register(new RegisterRequest("name1", "pass1", "email1"));
        var game = facade.createGame(result.authToken(), new CreateGameRequest("gameName"));
        assertDoesNotThrow(()-> facade.joinGame(result2.authToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, game.gameID())));
        Assertions.assertTrue(true);
    }

     @Test
    public void negativeJoinTest() throws ResponseException {
        var result = facade.register(new RegisterRequest("name", "pass", "email"));
        var result2 = facade.register(new RegisterRequest("name1", "pass1", "email1"));
        var game = facade.createGame(result.authToken(), new CreateGameRequest("gameName"));
        facade.joinGame(result2.authToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, game.gameID()));
        assertThrows(ResponseException.class, ()-> facade.joinGame(result.authToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, game.gameID())));
    }

     @Test
    public void postiveListTest() throws ResponseException {
        var result = facade.register(new RegisterRequest("name", "pass", "email"));
        facade.createGame(result.authToken(), new CreateGameRequest("gameName"));
        facade.createGame(result.authToken(), new CreateGameRequest("gameName1"));
        assertDoesNotThrow(()-> facade.listGames(result.authToken()));
    }

     @Test
    public void negativeListTest() throws ResponseException {
        var result = facade.register(new RegisterRequest("name", "pass", "email"));
        facade.createGame(result.authToken(), new CreateGameRequest("gameName"));
        facade.createGame(result.authToken(), new CreateGameRequest("gameName1"));
        assertThrows(ResponseException.class,()-> facade.listGames(null));
    }

    // clear 
     @Test
    public void postiveClearTest() throws ResponseException {
        var result = facade.register(new RegisterRequest("name", "pass", "email"));
        facade.createGame(result.authToken(), new CreateGameRequest("gameName"));
        facade.createGame(result.authToken(), new CreateGameRequest("gameName1"));
        facade.clear();
        assertThrows(ResponseException.class,() -> facade.login(new LoginRequest("name", "pass")));
    }

    @Test
    public void negativeClearTest() throws ResponseException {
        assertDoesNotThrow(()->facade.clear());
    }
}
