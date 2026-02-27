package client;

import java.util.Arrays;

import exception.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;

public class PreClient implements Client {
    private final ServerFacade server;
    public String authToken;

    public PreClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    private void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public String getAuthToken() {
        return this.authToken;
    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            var result = server.login(new LoginRequest(params[0], params[1]));
            setAuthToken(result.authToken());
            return "Login successful.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            var result = server.register(new RegisterRequest(params[0], params[1], params[2]));
            setAuthToken(result.authToken());
            return "Registration successful.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    @Override
    public String help() {
        return """
                - login <USERNAME> <PASSWORD> - to login
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - quit
                - help
                """;
    }
}
