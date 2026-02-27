package client;

import java.util.Arrays;
import java.util.Scanner;

import javax.management.Notification;

import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;

public class Repl {
    private final PreClient preClient;
    private final PostClient postClient;
    private final GameClient gameClient;
    private State state = State.SIGNEDOUT;

    public Repl(String serverUrl) {
        this.preClient = new PreClient(serverUrl);
        this.postClient = new PostClient(serverUrl);
        this.gameClient = new GameClient(serverUrl);
    }

    public void run() {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        System.out.print(preClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
    

       public void notify(Notification notification) {
        System.out.println(RED + notification.message());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }


}
