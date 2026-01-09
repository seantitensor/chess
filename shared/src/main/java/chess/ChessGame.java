package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + ((currentTeamColor == null) ? 0 : currentTeamColor.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessGame other = (ChessGame) obj;
        if (board == null) {
            if (other.board != null)
                return false;
        } else if (!board.equals(other.board))
            return false;
        if (currentTeamColor != other.currentTeamColor)
            return false;
        return true;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private ChessBoard board;
    private ChessGame.TeamColor currentTeamColor;

    public ChessGame() {
        this.board = new ChessBoard();
        this.currentTeamColor = TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeamColor = team;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null || piece.getTeamColor() != currentTeamColor) {
            throw new InvalidMoveException("Select a valid piece to move.");
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Move is not valid.");
        }

        if (move.getPromotionPiece() != null) {
            ChessPiece promotedChessPiece = new ChessPiece(currentTeamColor,move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedChessPiece);
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }


    }

    private ChessPosition kingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                    return position;
                }
            }
        }
        return null; // King not found (should not happen in a valid game)

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = kingPosition(teamColor);
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row,col));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> oppMoves = piece.pieceMoves(board, new ChessPosition(row,col));
                    for (ChessMove move: oppMoves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return checkMovesToStopCheckmate(teamColor);
    }

    private boolean checkMovesToStopCheckmate(TeamColor teamColor) {
        for (int row = 1; row <= 9; row ++) {
            for (int col = 1; col <= 9; col ++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row,col));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, new ChessPosition(row,col));
                    for (ChessMove move: moves) {
                        return stopCheckCheck(move);
                    }
                }
            }
        }
        return false;
    }

    private boolean stopCheckCheck(ChessMove move) {
        ChessBoard testBoard = (ChessBoard) board.clone();
        ChessPiece piece = testBoard.getPiece(move.getStartPosition());




    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
