package client;

import java.util.Arrays;

import exception.ResponseException;
import request.CreateGameRequest;

public class PostClient implements Client {
    private final ServerFacade server;
    private String authToken;

    public PostClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "quit" -> "quit";
                case "create" -> create(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    
    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            server.createGame(authToken, new CreateGameRequest( params[0]));
            return "Game created.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <NAME>");
    }

    @Override
    public String help() {
        return """
                - create <NAME>  - create a game
                - list - list all games
                - join <ID> [WHITE|BLACK] - join a game
                - observe <ID> - observe a game
                - logout
                - quit
                - help
                """;
    }
}
