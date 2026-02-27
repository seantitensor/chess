package client;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;
import javax.management.Notification;

import org.junit.platform.commons.util.Preconditions;

import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;

public class Repl {
    private final PreClient preClient;
    private final PostClient postClient;
    private State state = State.SIGNEDOUT;

    public Repl(String serverUrl) throws ResponseException {
        this.preClient = new PreClient(serverUrl);
        this.postClient = new PostClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ 240 Chess");
        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")) {
            var client = (state == State.SIGNEDOUT) ? preClient : postClient;
            System.out.print(client.help());
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result.equals("Login successful.") || result.equals("Registration successful.")) {
                    postClient.setAuthToken(preClient.getAuthToken());
                    state = State.SIGNEDIN;
                }
                if (result.equals("Logout successful.")) {
                    state = State.SIGNEDOUT;
                }
                System.out.print(SET_TEXT_COLOR_GREEN + result + RESET_TEXT_COLOR);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg + RESET_TEXT_COLOR);
            }
        }
        System.out.println();
    }
    
    public void notify(Notification notification) {
        System.out.println(SET_TEXT_COLOR_RED + notification.message() + RESET_TEXT_COLOR);
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
