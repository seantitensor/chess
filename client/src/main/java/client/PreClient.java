package client;

import java.util.Arrays;

import exception.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;

public class PreClient {
    private final ServerFacade server;

    public PreClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "Help" -> help();
                case "Login" -> login(params);
                case "Register" -> register(params);
                case "Quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            server.login(new LoginRequest(params[0], params[1]));
            return "Login successful.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <USERNAME> <PASSWORD>");
    }


    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            server.register(new RegisterRequest(params[0], params[1], params[2]));
            return "Registration successful.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }



    public String help() {
        return """
                - login <USERNAME> <PASSWORD> - to login
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - quit
                - help
                """;
    }
}
