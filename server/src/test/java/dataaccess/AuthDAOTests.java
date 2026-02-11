/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package dataaccess;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import dataaccess.auth.SqlAuthDAO;
 
/**
 *
 * @author seantitensor
 */
public class AuthDAOTests {

     public AuthDAOTests() {
    }

    private final SqlAuthDAO authDAO = new SqlAuthDAO();

    @BeforeEach
    void setup() {
        authDAO.clearAuth();
    }

    @Test
    @DisplayName("positive create")
    @Order(1)
    void positiveCreateAuthTest() {
        authDAO.createAuth("testToken","testUser");
        assertNotNull(authDAO.getAuthData("testToken"));
    }

    @Test
    @DisplayName("negative create")
    @Order(2)
    void negativeCreateAuthTest() {
        authDAO.createAuth("testToken","testUser");
        assertNull(authDAO.getAuthData("notToken"));
    }

    @Test
    @DisplayName("positive get auth")
    @Order(3)
    void positiveGetAuthTest() {
        authDAO.createAuth("testToken","testUser");
        authDAO.createAuth("Token1","testUser");
        assertNotNull(authDAO.getAuthData("Token1"));
    }

    @Test
    @DisplayName("Negative get auth")
    @Order(4)
    void negativeGetAuthTest() {
        authDAO.createAuth("testToken","testUser");
        authDAO.createAuth("Token1","testUser");
        assertNull(authDAO.getAuthData("Token"));
    }

    @Test
    @DisplayName("positive delete auth")
    @Order(5)
    void positiveDeleteAuthTest() {
        authDAO.createAuth("testToken","testUser");
        authDAO.createAuth("Token1","testUser");
        authDAO.deleteAuth("Token1");
        assertNull(authDAO.getAuthData("Token1"));
    }

    @Test
    @DisplayName("Negative delete auth")
    @Order(6)
    void negativeDeleteAuthTest() {
        authDAO.createAuth("testToken","testUser");
        authDAO.createAuth("Token1","testUser");
        assertDoesNotThrow(() -> authDAO.deleteAuth("1w"));
        
    }

    @Test
    @DisplayName("positive clear auth")
    @Order(7)
    void positiveClearAuthTest() {
        authDAO.createAuth("testToken","testUser");
        authDAO.createAuth("Token1","testUser");
        authDAO.clearAuth();
        assertNull(authDAO.getAuthData("Token1"));
    }

    @Test
    @DisplayName("Negative clear auth")
    @Order(8)
    void negativeClearAuthTest() {
        assertDoesNotThrow(()->authDAO.clearAuth());
    }
}