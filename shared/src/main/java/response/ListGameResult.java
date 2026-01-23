package response;

public record ListGameResult(
    Integer gameID, 
    String whiteUsername, 
    String blackUsername, 
    String gameName
) {}