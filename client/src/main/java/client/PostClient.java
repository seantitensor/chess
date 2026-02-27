package client;

public class PostClient {
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
