package chess.movecalculators;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
public class PawnMoveCalculator implements PieceMoveCalculator {
    
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor pieceColor = myPiece.getTeamColor();
        
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int[][] captures;
        
        if (pieceColor == ChessGame.TeamColor.WHITE){
            captures = new int[][] {
                {1, -1},
                {1, 1}
            };
        } else {
            captures = new int[][] {
                {-1, -1},
                {-1, 1} 
            };
        }

        // check for opponent piece to capture
        for (int[] capture : captures) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            row += capture[0];
            col += capture[1];

            if (row < 1 || row > 8 || col < 1 || col > 8) {continue;}

            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

            if (pieceAtNewPosition != null) {
                if (pieceAtNewPosition.getTeamColor() != pieceColor) {
                    if (pieceColor == ChessGame.TeamColor.WHITE  && row == 8) {
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    } else if (pieceColor == ChessGame.TeamColor.BLACK  && row == 1) {
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                } else {
                    continue;
                }
            }
        }

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        
        // start double move
        if (row == startRow) {
            int newRow = direction * 2;
            ChessPosition newPosition = new ChessPosition(row + newRow, col);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
            ChessPosition intermediatePosition = new ChessPosition(row + direction, col);
            ChessPiece intermediateChessPiece = board.getPiece(intermediatePosition);
            if (intermediateChessPiece == null){
                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        // normal move
        row += direction;
        if (row < 1 || row > 8 || col < 1 || col > 8) {return moves;}
        
        ChessPosition newPosition = new ChessPosition(row, col);
        ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
        if (pieceAtNewPosition == null) {
            if (pieceColor == ChessGame.TeamColor.WHITE  && row == 8) {
                moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
            } else if (pieceColor == ChessGame.TeamColor.BLACK  && row == 1) {
                moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return moves;
    }
}