package client;

import java.util.Arrays;

import exception.ResponseException;

public class PreClient {


     public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "Help" -> help();
                case "Login" -> rescuePet(params);
                case "Register" -> listPets();
                case "Quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
            ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname>");
    }



    public String help() {
        return """
                - login <USERNAME> <PASSWORD> - to login
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - quit
                - help
                """;
    }
}
