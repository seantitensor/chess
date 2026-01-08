package chess.movecalculators;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
public class KnightMoveCalculator implements PieceMoveCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);

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

        for (int[] jump : jumps) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            row += jump[0];
            col += jump[1];

            if (row < 1 || row > 8 || col < 1 || col > 8) { continue; }

            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

            if (pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != myPiece.getTeamColor()) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return moves;
    }
}