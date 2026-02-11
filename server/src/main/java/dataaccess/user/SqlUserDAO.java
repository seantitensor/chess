package dataaccess.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SqlDAO;
import model.UserData;

public class SqlUserDAO extends SqlDAO implements UserDAO {

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, user.username(), user.password(), user.email());
        } catch (DataAccessException e) {
            throw new DataAccessException("couldn't connect to the db");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {            
            var statement = "SELECT username, password, email FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("couldn't connect to the db");
        }
        return null;
    }

    @Override
    public void clear() {
        var statement = "TRUNCATE users";
        executeUpdate(statement);    
    }
    
}
