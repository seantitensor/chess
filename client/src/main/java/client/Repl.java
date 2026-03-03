package client;

import java.util.Scanner;

import exception.ResponseException;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

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

        System.out.print(SET_TEXT_COLOR_MAGENTA + preClient.help());
        while (!result.equals("quit")) {
            var client = (state == State.SIGNEDOUT) ? preClient : postClient;
            printPrompt(state);
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

    private void printPrompt(State state) {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + state + "] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
