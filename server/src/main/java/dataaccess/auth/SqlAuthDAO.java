package dataaccess.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SqlDAO;
import model.AuthData;


public class SqlAuthDAO extends SqlDAO implements AuthDAO {
  
    @Override
    public void createAuth(String authToken, String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
            executeUpdate(statement, authToken, username);
        } catch (SQLException e) {
            throw new DataAccessException("error executing createAuth: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {  
            var statement = "SELECT username, authToken FROM auths WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("error executing getAuthData: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths WHERE authToken = ?";
            executeUpdate(statement, authToken);
        } catch (SQLException e) {
            throw new DataAccessException("couldn't connect to the db");
        }
    }

    @Override
    public void clearAuth() {
        var statement = "TRUNCATE auths";
        executeUpdate(statement);   
    }
}
