package dataaccess.user;

import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccessException;
import model.UserData;

public class LocalUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
