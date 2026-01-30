package chess.movecalculators;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class QueenMoveCalculator implements PieceMoveCalculator {
    
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        int[][] directions = {
            {1,1}, 
            {1,-1}, 
            {-1,1}, 
            {-1,-1},
            {1,0}, 
            {-1,0}, 
            {0,1}, 
            {0,-1}
        };
        return slidingMoves(board, myPosition, directions);
    }
}
