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
import dataaccess.user.LocalUserDAO;
import dataaccess.user.UserDAO;
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
    @DisplayName("register a new user")
    public void registerUser() {

    }

    @Test
    @DisplayName("register a bad user")
    public void registerBadUser() {

    }

    @Test
    @DisplayName("login good user")
    public void loginUser() {

    }

    @Test
    @DisplayName("login bad user")
    public void loginBadUser() {

    }

        @Test
    @DisplayName("logout user")
    public void logoutUser() {

    }

    @Test
    @DisplayName("logout empty user")
    public void logoutBadUser() {

    }

    @Test
    @DisplayName("ClearDB")
    public void clearUserDB() {
        
    }

    @Test
    @DisplayName("ClearDB but for bad user")
    public void clearBadUserDB() {
        
    }


}