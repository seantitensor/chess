package client;

import java.util.Arrays;

import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import exception.ResponseException;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import websocket.messages.ServerMessage;

public class GameClient implements Client, NotificationHandler {

    private final ServerFacade server;
    private WebsocketFacade ws;
    private String authToken;

    public GameClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void setWebsocketFacade(WebsocketFacade ws) {
        this.ws = ws;
        
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
        return "Left the match.";
    }

    public String resign() throws ResponseException {
        return "Resigned.";

    }

    public String redraw() throws ResponseException {
        return "";
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch(serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                System.out.println(SET_TEXT_COLOR_MAGENTA + serverMessage);
                printPrompt(State.INGAME);
            }
            case ERROR -> {
                System.out.println(SET_TEXT_COLOR_RED + serverMessage);
                printPrompt(State.INGAME);
            }
            case NOTIFICATION -> {
                System.out.println(SET_TEXT_COLOR_GREEN + serverMessage);
                printPrompt(State.INGAME);
            }
        }
    }

    private void printPrompt(State state) {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + state + "] >>> " + SET_TEXT_COLOR_GREEN);
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
