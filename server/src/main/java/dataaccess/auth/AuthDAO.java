package dataaccess.auth;

import dataaccess.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    // create auth Token
    void createAuth(String authToken, String username) throws DataAccessException;
    // get auth token
    AuthData getAuthData(String authToken) throws DataAccessException;
    // delete auth token
    void deleteAuth(String authToken) throws DataAccessException;
    // clear auth tokens
    void clearAuth();
}
