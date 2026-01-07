package chess.movecalculators;

import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

public interface PieceMoveCalculator {

    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
