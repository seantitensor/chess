package chess.movecalculators;

import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
public class KnightMoveCalculator implements PieceMoveCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        int[][] jumps = {
            {2,1}, 
            {2,-1}, 
            {-2,1}, 
            {-2,-1},
            {1,2},
            {1,-2},
            {-1,2},
            {-1,-2}
        };
        return normalMoves(board, myPosition, jumps);
    }
}