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
        var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try {
            executeUpdate(statement, authToken, username);
        } catch (DataAccessException e) {
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
        var statement = "DELETE FROM auths WHERE authToken = ?";
        try {
            executeUpdate(statement, authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("couldn't connect to the db");
        }
    }

    @Override
    public void clearAuth() {
        var statement = "TRUNCATE auths";
        executeUpdate(statement);   
    }
}
