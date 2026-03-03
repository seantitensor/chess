package client;

import java.util.Arrays;

import chess.ChessGame;
import exception.ResponseException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.ListGameResult;
import ui.Board;

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
                case "quit" -> "quit";
                case "create" -> create(params);
                case "join" -> join(params);
                case "list" -> list();
                case "logout" -> logout();
                case "observe" -> observe(params);
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

    public String join(String... params) throws ResponseException {
        if (params.length == 2) {
            server.joinGame(authToken, new JoinGameRequest(ChessGame.TeamColor.valueOf(params[1].toUpperCase()), Integer.valueOf(params[0])));
            Board.drawBoard(System.out, (params[1].toUpperCase().equals("WHITE")));
            return "Joined game.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> [WHITE|BLACK]");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            server.joinGame(authToken, new JoinGameRequest(null, Integer.valueOf(params[0])));
            Board.drawBoard(System.out, true);
            return "Observing game.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> [WHITE|BLACK]");
    }

    public String list() throws ResponseException {
        var result = server.listGames(authToken);
        var games = result.games();
        if (games == null || games.isEmpty()) {
            return "no games available";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        for (ListGameResult game : games) {
            stringBuilder.append(String.format("%d. ID: %d Game Name: %s (White: %s, Black: %s)\n", 
                i++,
                game.gameID(),
                game.gameName(), 
                game.whiteUsername() != null ? game.whiteUsername() : "Empty", 
                game.blackUsername() != null ? game.blackUsername() : "Empty"
            ));
        }
        return stringBuilder.toString();
    }

    public String logout() throws ResponseException {
        server.logout(authToken);
        authToken = null;
        return "Logout successful.";
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
