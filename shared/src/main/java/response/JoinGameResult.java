package response;
import chess.ChessGame;

public record JoinGameResult(
    ChessGame.TeamColor playerColor,
     String gameID
) {}
