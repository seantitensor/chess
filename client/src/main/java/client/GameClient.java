package client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

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
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class GameClient implements Client, NotificationHandler {
    private final ServerFacade server;
    private WebsocketFacade ws;
    private String authToken;
    private Integer gameID;
    private Board board = new Board();
    private boolean isWhitePlayer;
    private boolean isObserving;

    public GameClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void setWhitePlayer(boolean isWhitePlayer) {
        this.isWhitePlayer = isWhitePlayer;
    }

    public void setIsObserving(boolean isObserving) {
        this.isObserving = isObserving;
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

    private String moves(String... params) throws ResponseException {
        if (params.length == 1) {
            ChessPosition pos = parsePostion(params[0]);
            if (pos != null) {
                ChessPiece piece = board.chessBoard.getPiece(pos);
                if (piece == null) {
                    return "no piece at position " + pos.toString();
                }
                Collection<ChessMove> moves = piece.pieceMoves(board.chessBoard, pos);
                this.board.drawBoard(System.out, isWhitePlayer, moves);
                return "";
            }
        }
        return "Add a position to see the available moves";
    }

    private String move(String... params) throws ResponseException {
        if (isObserving == true) {
            return "Observers cannot make a move";
        }
        if (params.length == 2) {
            ChessPosition from = parsePostion(params[0]);
            ChessPosition to = parsePostion(params[1]);
            ChessPiece.PieceType promotion = null;
            ChessPiece piece = board.chessBoard.getPiece(from);
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN && ((!isWhitePlayer && to.getRow() == 1) || (isWhitePlayer && to.getRow() == 8))) {
                promotion = promote();
            }
            ChessMove move = new ChessMove(from, to, promotion);
            if (from != null && to != null) {
                ws.makeMove(authToken, gameID, move);
                return "Move sent...";
            }
        }
        return "make sure to add a to and from square.";
    }

    private String leave() throws ResponseException {
        ws.leave(authToken, gameID);
        clearClient();
        return "Left the match.";
    }

    private String resign() throws ResponseException {
        if (isObserving == true) {
            return "Observers cannot resign. leave instead";
        }
        System.out.print("Are you sure you want to resign? (yes/no): ");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("yes")) {
                ws.resign(authToken, gameID);
                return "Resigned.";
        } else {
            return "Resignation cancelled.";
        }
    }

    public String redraw() throws ResponseException {
        this.board.drawBoard(System.out, isWhitePlayer, null);
        return "\n";
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch(serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                var loadGame = (LoadGameMessage) serverMessage;
                ChessGame game = loadGame.getGame();
                this.board.chessBoard = game.getBoard();
                this.setWhitePlayer(isWhitePlayer);
                System.out.println("\n");
                this.board.drawBoard(System.out, isWhitePlayer, null);
                printPrompt(State.INGAME);
            }
            case ERROR -> {
                var error = (ErrorMessage) serverMessage;
                System.out.println(SET_TEXT_COLOR_RED + error.getMessage());
                printPrompt(State.INGAME);
            }
            case NOTIFICATION -> {
                var notif = (NotificationMessage) serverMessage;
                System.out.println(SET_TEXT_COLOR_GREEN + notif.getMessage());
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

    private void clearClient() {
        this.gameID = null;
        this.board = new Board();
        this.isWhitePlayer = true;
        this.isObserving = false;
    }

    private ChessPiece.PieceType promote() {
        System.out.print("Promotion! Choose a piece (Q, N, R, B): ");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine().trim().toUpperCase();
        return switch (choice) {
            case "N" -> ChessPiece.PieceType.KNIGHT;
            case "R" -> ChessPiece.PieceType.ROOK;
            case "B" -> ChessPiece.PieceType.BISHOP;
            case "Q" -> ChessPiece.PieceType.QUEEN;
            default -> promote();
        };
    }
}
