package chess.movecalculators;

import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

public class KingMoveCalculator implements PieceMoveCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        int[][] steps = {
            {1,1}, 
            {1,-1},
            {1,0},
            {0,1}, 
            {0,-1}, 
            {-1,0},
            {-1,1}, 
            {-1,-1}
        };
        return normalMoves(board, myPosition, steps);
    }
}
