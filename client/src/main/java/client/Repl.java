package client;

import java.util.Scanner;

import com.google.gson.Gson;

import client.websocket.WebsocketFacade;
import exception.ResponseException;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class Repl {
    private final PreClient preClient;
    private final PostClient postClient;
    private final GameClient gameClient;
    private State state = State.SIGNEDOUT;
    private final String serverUrl;
    private final Gson gson = new Gson();

    public Repl(String serverUrl) throws ResponseException {
        this.preClient = new PreClient(serverUrl);
        this.postClient = new PostClient(serverUrl);
        this.gameClient = new GameClient(serverUrl);
        this.serverUrl = serverUrl;
    }

    public void run() {
        System.out.println("♕ 240 Chess");
        Scanner scanner = new Scanner(System.in);
        var result = "";

        System.out.print(SET_TEXT_COLOR_MAGENTA + preClient.help());
        while (!result.equals("quit")) {
            var client = switch(state){
                case SIGNEDOUT -> preClient;
                case SIGNEDIN -> postClient;
                case INGAME -> gameClient;
            };

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

                if (result.equals("Joined game.") || result.equals("Observing game.")) {
                    gameClient.setAuthToken(preClient.getAuthToken());
                    if (result.equals("Observing game.")) {
                        gameClient.setIsObserving(true);
                    }
                    gameClient.setGameID(postClient.getGameID());
                    gameClient.setWhitePlayer(postClient.getWhitePlayer());
                    state = State.INGAME;
                    try {
                        var ws = new WebsocketFacade(serverUrl, gameClient);
                        gameClient.setWebsocketFacade(ws);
                        gameClient.connectToWS();
                    } catch (Exception e) {
                        System.out.print(SET_TEXT_COLOR_RED + "WebSocket failed: " + e.getMessage());
                    }
                }

                if (result.equals("Left the match.") || result.equals("Resigned.")) {
                    state = State.SIGNEDIN;
                }
                
                System.out.print(SET_TEXT_COLOR_GREEN + result + RESET_TEXT_COLOR);
            } catch (Throwable e) {
                var msg = gson.toJson(e);
                System.out.print(SET_TEXT_COLOR_RED + msg + RESET_TEXT_COLOR);
            }
        }
        System.out.println();
    }

    private void printPrompt(State state) {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + state + "] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
