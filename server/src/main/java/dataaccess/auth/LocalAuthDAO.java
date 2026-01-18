package dataaccess.auth;

import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccessException;
import model.AuthData;

public class LocalAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authDatas = new HashMap<>();

    @Override
    public void createAuth(String authToken, String username) throws DataAccessException {
        authDatas.put(authToken, new AuthData(username, authToken));
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authDatas.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDatas.remove(authToken);
    }

    @Override
    public void clearAuth() {
        authDatas.clear();
    }

    
}
