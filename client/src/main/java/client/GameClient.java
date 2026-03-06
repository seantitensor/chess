package client;

import java.util.Arrays;
import java.util.Collection;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import exception.ResponseException;
import ui.Board;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

public class GameClient implements Client, NotificationHandler {
    private final ServerFacade server;
    private WebsocketFacade ws;
    private String authToken;
    private Integer gameID;
    private Board board = new Board();
    private boolean isWhitePlayer;



    public GameClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void setWhitePlayer(boolean isWhitePlayer) {
        this.isWhitePlayer = isWhitePlayer;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public void setWebsocketFacade(WebsocketFacade ws) {
        this.ws = ws;
    }

    public void connectToWS() throws ResponseException {
        ws.connect(authToken, gameID);
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
        if (params.length == 1) {
            ChessPosition pos = parsePostion(params[0]);
            if (pos != null) {
                ChessPiece piece = board.chessBoard.getPiece(pos);
                Collection<ChessMove> moves = piece.pieceMoves(board.chessBoard, pos);
                board.drawMoves(moves);
                return "";
            }
        }
        return "";
    }

    public String move(String... params) throws ResponseException {
        if (params.length == 2) {
            ChessPosition from = parsePostion(params[0]);
            ChessPosition to = parsePostion(params[1]);
            ChessMove move = new ChessMove(from, to, null);
            if (from != null && to != null) {
                ws.makeMove(authToken, gameID, move);
                return "Move sent...";
            }
        }
        return "Invalid move.";
    }

    public String leave() throws ResponseException {
        ws.leave(authToken, gameID);
        return "Left the match.";
    }

    public String resign() throws ResponseException {
        ws.resign(authToken, gameID);
        return "Resigned.";
    }

    public String redraw() throws ResponseException {
        board.drawBoard(System.out, isWhitePlayer);
        return "";
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch(serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                var loadGame = (LoadGameMessage) serverMessage;
                ChessGame game = loadGame.getGame();
                this.board.chessBoard = game.getBoard();
                this.setWhitePlayer(isWhitePlayer);
                this.board.drawBoard(System.out, isWhitePlayer);
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

    // helper functions
    private ChessPosition parsePostion(String input) throws ResponseException {
        if (input.length() != 2) return null;
        int col = input.charAt(0) - 'a' + 1;
        int row = input.charAt(1) - '1' + 1;
        return new ChessPosition(row, col);
    }
}
