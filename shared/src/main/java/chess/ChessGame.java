package chess;

import java.util.ArrayList;
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
        BLACK;

        public TeamColor opp() {
            return (this == WHITE) ? BLACK : WHITE;
        }
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

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        try {
            if (board.getPiece(startPosition) == null) {
                return null;
            } else {
                Collection<ChessMove> invalidMoves = new ArrayList<>();
                ChessPiece piece = board.getPiece(startPosition);
                Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
                TeamColor teamColor = piece.getTeamColor();
                
                for (ChessMove move: moves) {
                    ChessBoard temp = board;
                    ChessBoard clone = (ChessBoard) board.clone();
                    board = clone;
                    board.addPiece(move.getStartPosition(), null);
                    board.addPiece(move.getEndPosition(), piece);
                    if (isInCheck(teamColor)) {
                        invalidMoves.add(move);
                    }
                    board = temp;
                }
                moves.removeAll(invalidMoves);
                return moves;
            }
        } catch(CloneNotSupportedException e) {
            throw new AssertionError("can't clone: " + this.board, e);
        }
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
            throw new InvalidMoveException("select a valid piece to move.");
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("move is not valid.");
        }

        if (move.getPromotionPiece() != null) {
            ChessPiece promotedChessPiece = new ChessPiece(currentTeamColor,move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedChessPiece);
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }

        board.addPiece(move.getStartPosition(), null);

        if (piece.getTeamColor().equals(TeamColor.WHITE)) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> oppMoves = allMoves(teamColor.opp());
        for (ChessMove move: oppMoves) {
            if (move.getEndPosition().equals(kingPosition(teamColor))) {
                return true;
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
        return !checkMovesToStopCheckmate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (ChessMove move: allMoves(teamColor)) {
            if (!validMoves(move.getStartPosition()).isEmpty()) {
                return false;
            }
        }
        return true;
    }

//Private methods

    /**
     * Determines if there are moves that can stop checkmate.
     *
     * @param teamColor which team to check for checkmate
     * @return True if there is a move that can stop checkmate
     */
    private boolean checkMovesToStopCheckmate(TeamColor teamColor) {
        Collection<ChessMove> moves = allMoves(teamColor);
        
        for (ChessMove move: moves) {
            try {
                if (stopCheckCheck(move, teamColor)) {
                    return true;
                }
            } catch (InvalidMoveException e) {
                continue;
            }
        }
        return false;
    }

    /**
     * Determines if there are moves that can stop checkmate.
     *
     * @param teamColor which team to check for checkmate
     * @param move the move to check if it can stop check
     * @return True if there is a move that can stop checkmate
     * @throws InvalidMoveException if the move is invalid
     */
    private boolean stopCheckCheck(ChessMove move, TeamColor teamColor) throws InvalidMoveException {
        ChessBoard temp = board;
        try {
            this.board = (ChessBoard) board.clone();
            makeMove(move);
            return (!isInCheck(teamColor));
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("can't clone: " + this.board, e);
        } finally {
            this.board = temp;
        }
    }

    /**
     * Determines the position of the king for a given team.
     *
     * @param teamColor which team to check the king position for
     * @return The position of the king for the given team, or null if not found
     */
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
        return null;
    }

    /**
     * Gets all possible moves for a given team
     *
     * @param teamColor which team to get all moves for
     * @return Collection of all possible moves for the given team
     */
    private Collection<ChessMove> allMoves(TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row,col));
                if (piece != null && piece.getTeamColor().equals(teamColor)) {
                    moves.addAll(piece.pieceMoves(board, new ChessPosition(row,col)));
                }
            }
        }
        return moves;
    }
}
