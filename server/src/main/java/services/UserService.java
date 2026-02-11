package services;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import dataaccess.auth.AuthDAO;
import dataaccess.user.UserDAO;
import exceptions.AlreadyTakenException;
import exceptions.UnauthorizedException;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResult;
import response.RegisterResult;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) {
        //check to see if username is already taken
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Username already taken.");
        }
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());

        UserData newUser = new UserData(request.username(), hashedPassword, request.email());
        userDAO.createUser(newUser);

        String token = generateToken();
        authDAO.createAuth(token, request.username());

        return new RegisterResult(request.username(), token);
    }


    public LoginResult login(LoginRequest request) {
        UserData user = userDAO.getUser(request.username());
        if (user == null) {
            throw new UnauthorizedException("No user with that username");
        }
        if (!BCrypt.checkpw(request.password(), user.password())) {
            throw new UnauthorizedException("Password is incorrect");
        }

        String authToken = generateToken();
        authDAO.createAuth(authToken, request.username());
        return new LoginResult(request.username(), authToken);
    }
    
    public void logout(String authToken) {
        if (authDAO.getAuthData(authToken) == null) {
            throw new UnauthorizedException("Unauthorized ");
        }
        authDAO.deleteAuth(authToken);
    }

    public void clearDB() {
        userDAO.clear();
        authDAO.clearAuth();
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
