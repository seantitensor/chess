/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dataaccess.auth.AuthDAO;
import dataaccess.auth.LocalAuthDAO;
import dataaccess.game.GameDAO;
import dataaccess.game.LocalGameDAO;
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

     public GameServiceTests() {
    }

    @BeforeEach
    public void setup() {
        gameDAO = new LocalGameDAO();
        authDAO = new LocalAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
        authToken = "test-token";
        authDAO.createAuth(authToken, "test-user");
    }

    @Test
    @DisplayName("create a Good Game")
    public void createGoodGame() {

    }

    @Test
    @DisplayName("create a bad Game")
    public void createBadGame() {

    }

    @Test
    @DisplayName("get empty list games")
    public void getEmptyList() {

    }

    @Test
    @DisplayName("get list games")
    public void getGameList() {

    }

        @Test
    @DisplayName("Join valid Game")
    public void joinGoodGame() {

    }

    @Test
    @DisplayName("Join invalid Game")
    public void joinBadGame() {

    }

    @Test
    @DisplayName("ClearDB")
    public void clearGameDB() {

    }

    @Test
    @DisplayName("ClearDB but for bad user")
    public void clearBadGameDB() {

    }


}