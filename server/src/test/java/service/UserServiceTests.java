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

import dataaccess.auth.AuthDAO;
import dataaccess.auth.LocalAuthDAO;
import dataaccess.user.LocalUserDAO;
import dataaccess.user.UserDAO;
import exceptions.AlreadyTakenException;
import exceptions.UnauthorizedException;
import request.LoginRequest;
import request.RegisterRequest;
import response.RegisterResult;
import services.UserService;
 
/**
 *
 * @author seantitensor
 */
public class UserServiceTests {

    public UserServiceTests() {}
    private UserService userService;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private String authToken;

    @BeforeEach
    public void setup() {
        userDAO = new LocalUserDAO();
        authDAO = new LocalAuthDAO();
        userService = new UserService(userDAO, authDAO);
        authToken = "test-token";
        authDAO.createAuth(authToken, "test-user");
    }

    @Test
    @DisplayName("positive: register a new user")
    public void registerUser() {
        RegisterRequest req = new RegisterRequest("test-user", "test-password", "test-email");
        assertDoesNotThrow(() -> {
            RegisterResult result = userService.register(req);
            assertNotNull(result.authToken());
            assertEquals("test-user", result.username());
        });
    }

    @Test
    @DisplayName("negative: register a bad user")
    public void registerBadUser() {
        RegisterRequest req = new RegisterRequest("test-user", "test-password", "test-email");
        assertDoesNotThrow(() -> userService.register(req));
        assertThrows(AlreadyTakenException.class, () -> userService.register(req));
    }

    @Test
    @DisplayName("positive: login good user")
    public void loginUser() {
        RegisterRequest req = new RegisterRequest("test-user", "test-password", "test-email");
        RegisterResult result = userService.register(req);
        userService.logout(result.authToken());
        assertDoesNotThrow(() -> userService.login(new LoginRequest("test-user", "test-password")));
    }

    @Test
    @DisplayName("negative: login bad user")
    public void loginBadUser() {
        RegisterRequest req = new RegisterRequest("test-user", "test-password", "test-email");
        RegisterResult result = userService.register(req);
        userService.logout(result.authToken());
        assertThrows( UnauthorizedException.class, () -> userService.login(new LoginRequest("test", "test-password")));
    }

    @Test
    @DisplayName("positive: logout user")
    public void logoutUser() {
        RegisterRequest req = new RegisterRequest("test-user", "test-password", "test-email");
        RegisterResult result = userService.register(req);
        assertDoesNotThrow(()->userService.logout(result.authToken()));
    }

    @Test
    @DisplayName("negative: logout empty user")
    public void logoutBadUser() {
        RegisterRequest req = new RegisterRequest("test-user", "test-password", "test-email");
        userService.register(req);
        assertThrows(UnauthorizedException.class,()->userService.logout("Fake_auth_token"));
    }

    @Test
    @DisplayName("postive: ClearDB")
    public void clearUserDB() {
        RegisterRequest req = new RegisterRequest("test-user", "test-password", "test-email");
        RegisterResult result = userService.register(req);
        userService.logout(result.authToken());
        userService.clearDB();
        assertThrows(UnauthorizedException.class, () -> userService.login(new LoginRequest("test-user", "test-password")));
    }

    @Test
    @DisplayName("negative: ClearDB but for bad user")
    public void clearBadUserDB() {
        RegisterRequest req = new RegisterRequest("test-user", "test-password", "test-email");
        userService.register(req);
        assertDoesNotThrow(()->userService.clearDB());
    }
}