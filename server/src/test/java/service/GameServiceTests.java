/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import dataaccess.auth.AuthDAO;
import dataaccess.auth.SqlAuthDAO;
import dataaccess.game.GameDAO;
import dataaccess.game.SqlGameDAO;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import request.JoinGameRequest;
import response.ListResult;
import response.NewGameResult;
import services.GameService;
 
/**
 *
 * @author seantitensor
 */
public class GameServiceTests {

    private GameService gameService;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private String authToken;

    public GameServiceTests() {}

    @BeforeEach
    public void setup() {
        gameDAO = new SqlGameDAO();
        authDAO = new SqlAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
        gameDAO.clear();
        authDAO.clearAuth();
        authToken = "test-token";
        authDAO.createAuth(authToken, "test-user");
    }

    @Test
    @DisplayName("postive: create a Good Game")
    public void createGoodGame() {
        assertDoesNotThrow(() -> {
        NewGameResult result = gameService.createGame(authToken, "game_name");
        assertNotNull(result.gameID());
    });
    }

    @Test
    @DisplayName("negative: create a bad Game")
    public void createBadGame() {
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("fjdakfjdfdaf", "games"));
    }

    @Test
    @DisplayName("postive: get list games")
    public void getList() {
        gameService.createGame(authToken, "game_name");
        gameService.createGame(authToken, "game_name1");
        gameService.createGame(authToken, "game_name2");
        assertDoesNotThrow(()-> {
            ListResult result = gameService.getListGames(authToken);
            assertEquals(result.games().size(), 3);
            
        });
    }

    @Test
    @DisplayName("negative: get unauthrozed list games")
    public void getGameList() {
        assertThrows(UnauthorizedException.class, () -> gameService.getListGames("bad_auth"));
    }

        @Test
    @DisplayName("positive: Join valid Game")
    public void joinGoodGame() {
        NewGameResult result = gameService.createGame(authToken, "game_name");
        JoinGameRequest req = new JoinGameRequest(ChessGame.TeamColor.WHITE,result.gameID());
        assertDoesNotThrow(() -> gameService.joinGame(authToken,req));
    }

    @Test
    @DisplayName("negative: Join invalid Game")
    public void joinBadGame() {
        NewGameResult result = gameService.createGame(authToken, "game_name");
        JoinGameRequest req = new JoinGameRequest(ChessGame.TeamColor.WHITE, result.gameID());
        gameService.joinGame(authToken,req);
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(authToken, req));
    }

    @Test
    @DisplayName("ClearDB")
    public void clearGameDB() {
        gameService.createGame(authToken, "game_name");
        gameService.createGame(authToken, "game_name1");
        gameService.createGame(authToken, "game_name2");
        assertDoesNotThrow(()->gameService.clearDB());
    }

    @Test
    @DisplayName("ClearDB but for bad user")
    public void clearBadGameDB() {
        NewGameResult result = gameService.createGame(authToken, "game_name");
        JoinGameRequest req = new JoinGameRequest(ChessGame.TeamColor.WHITE,result.gameID());
        assertDoesNotThrow(()->gameService.clearDB());
        assertThrows(BadRequestException.class, () -> gameService.joinGame(authToken, req));
    }
}