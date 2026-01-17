package dataaccess.user;

import dataaccess.DataAccessException;
import model.UserData;

public interface UserDAO {
    //register
    void createUser(UserData user) throws DataAccessException;

    //login
    UserData getUser(String username) throws DataAccessException;
    
    //clear
    void clear();
}