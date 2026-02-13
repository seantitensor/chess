/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package dataaccess;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import dataaccess.user.SqlUserDAO;
import model.UserData;
 
/**
 *
 * @author seantitensor
 */
public class UserDAOTests {

     public UserDAOTests() {
    }


    private SqlUserDAO dao = new SqlUserDAO();

    @BeforeEach
    void setup() {
        dao.clear();
    }

    @Test
    @DisplayName("positive create")
    @Order(1)
    void positiveCreateUserTest() {
        UserData user = new UserData("sean","seanrocks","seanemail");
        dao.createUser(user);
        assertNotNull(dao.getUser("sean"));
    }

    @Test
    @DisplayName("negative create")
    @Order(2)
    void negativeCreateAuthTest() {
        UserData user = new UserData("sean","seanrocks","seanemail");
        dao.createUser(user);
        UserData user1 = new UserData("sean","seanrocks","seanemail");
        assertThrows(DataAccessException.class, ()-> dao.createUser(user1));
    }

    @Test
    @DisplayName("positive get user")
    @Order(3)
    void positiveGetUserTest() {
        UserData user = new UserData("sean","seanrocks","seanemail");
        dao.createUser(user);
        assertNotNull(dao.getUser("sean"));
    }

    @Test
    @DisplayName("Negative get user")
    @Order(4)
    void negativeGetAuthTest() {
        UserData user = new UserData("sean","seanrocks","seanemail");
        dao.createUser(user);
        assertNull(dao.getUser("seanrocks"));
    }

    @Test
    @DisplayName("positive clear user")
    @Order(5)
    void positiveClearUserTest() {
        UserData user = new UserData("sean","seanrocks","seanemail");
        dao.createUser(user);
        dao.clear();
        assertNull(dao.getUser("sean"));

    }

    @Test
    @DisplayName("Negative clear auth")
    @Order(8)
    void negativeClearAuthTest() {
        assertDoesNotThrow(()->dao.clear());
    }
}