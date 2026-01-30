package chess.movecalculators;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public interface PieceMoveCalculator {

    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    default Collection<ChessMove> slidingMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) { break; }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                else if (pieceAtNewPosition.getTeamColor() != myPiece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                }
                else {
                    break;
                }
            }
        }
        return moves;
    }

    default Collection<ChessMove> normalMoves(ChessBoard board, ChessPosition myPosition, int[][] steps) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        for (int[] step : steps) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            row += step[0];
            col += step[1];

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
