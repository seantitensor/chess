/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import dataaccess.game.SqlGameDAO;
import model.GameData;
 
/**
 *
 * @author seantitensor
 */
public class GameDAOTests {

    public GameDAOTests() {
    }

    private final SqlGameDAO dao = new SqlGameDAO();


    @BeforeEach
    void setup() {
        dao.clear();
    }

    @Test
    @DisplayName("positive create")
    @Order(1)
    void positiveCreateGameTest() {
        GameData game = new GameData(0,null,null,"game", null);
        int gameID = dao.createGame(game);
        assertNotNull(dao.getGame(gameID));
    }

    @Test
    @DisplayName("negative create")
    @Order(2)
    void negativeCreateGameTest() {
        GameData game = new GameData(0,null,null,"game", null);
        dao.createGame(game);
        dao.createGame(game);
        assertNotNull(dao.getGames());
    }

    @Test
    @DisplayName("positive get game")
    @Order(3)
    void positiveGetGameTest() {
        GameData game = new GameData(0,null,null,"game", null);
        int gameID = dao.createGame(game);
        assertNotNull(dao.getGame(gameID));
    }

    @Test
    @DisplayName("Negative get game")
    @Order(4)
    void negativeGetGameTest() {
        GameData game = new GameData(0,null,null,"game", null);
        int gameID = dao.createGame(game);
        assertNull(dao.getGame(0));
    }

    @Test
    @DisplayName("positive list games")
    @Order(5)
    void positiveListGamesTest() {
        GameData game = new GameData(0,null,null,"game", null);
        GameData game1 = new GameData(0,null,null,"game1", null);
        GameData game2 = new GameData(0,null,null,"game2", null);
        GameData game3 = new GameData(0,null,null,"game3", null);
        int gameID = dao.createGame(game);
        int gameID1 = dao.createGame(game1);
        int gameID2 = dao.createGame(game2);
        int gameID3 = dao.createGame(game3);
        assertEquals(4, dao.getGames().size());
    }

    @Test
    @DisplayName("Negative get games")
    @Order(6)
    void negativeListGamesTest() {
        GameData game = new GameData(0,null,null,"game", null);
        GameData game4 = new GameData(0,null,null,"game", null);
        GameData game1 = new GameData(0,null,null,"game1", null);
        GameData game2 = new GameData(0,null,null,"game2", null);
        GameData game3 = new GameData(0,null,null,"game3", null);
        assertEquals(0, dao.getGames().size());
    }

    @Test
    @DisplayName("positive update games")
    @Order(7)
    void positiveUpdateGamesTest() {
        GameData game = new GameData(0,null,null,"game", null);
        int gameID = dao.createGame(game);
        GameData doneGame = dao.getGame(gameID);
        GameData newGame = new GameData(doneGame.gameID(),null,"sean",doneGame.gameName(),doneGame.game());
        dao.updateGame(newGame);
        assertEquals("sean", dao.getGame(gameID).blackUsername());
    }

    @Test
    @DisplayName("Negative get games")
    @Order(8)
    void negativeUpdateGamesTest() {
        GameData game = new GameData(0,null,null,"game", null);
        int gameID = dao.createGame(game);
        GameData doneGame = dao.getGame(gameID);
        GameData newGame = new GameData(0,null,null,"game1",doneGame.game());
        dao.updateGame(newGame);
        assertEquals("game", dao.getGame(gameID).gameName());
    }

    @Test
    @DisplayName("positive clear game")
    @Order(9)
    void positiveClearGameTest() {
        dao.createGame(new GameData(0, null, null, "game", null));
        dao.clear();
        assertEquals(0, dao.getGames().size());
    }

    @Test
    @DisplayName("Negative clear games")
    @Order(10)
    void negativeClearGameTest() {
        assertDoesNotThrow(()->dao.clear());
    }
}