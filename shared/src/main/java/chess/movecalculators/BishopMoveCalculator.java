package chess.movecalculators;

import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;


public class BishopMoveCalculator implements PieceMoveCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        int[][] directions = {
            {1,1}, 
            {1,-1}, 
            {-1,1}, 
            {-1,-1}
        };
        return slidingMoves(board, myPosition, directions);
    }
}
