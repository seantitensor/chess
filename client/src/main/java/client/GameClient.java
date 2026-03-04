package client;

import java.util.Arrays;

import exception.ResponseException;

public class GameClient implements Client {

    private final ServerFacade server;
    private String authToken;

    public GameClient(String serverUrl) throws ResponseException {
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
                case "moves" -> moves(params);
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "redraw" -> redraw();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String moves(String... params) throws ResponseException {
        return "";

    }


    public String move(String... params) throws ResponseException {
        return "";

    }

    public String leave() throws ResponseException {
        return "";

    }

    public String resign() throws ResponseException {
        return "";

    }

    public String redraw() throws ResponseException {
        return "";
    }


    @Override
    public String help() {
        return """
                - moves <SQUARE>  - view moves for a given piece
                - move <SQUARE> <SQUARE> - move piece from one spot to another
                - leave - leave the game
                - resign - resign the game
                - redraw - redraw the game board
                - quit
                - help
                """;
    }
}
