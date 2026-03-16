package ui;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import static ui.EscapeSequences.BLACK_BISHOP;
import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.BLACK_KNIGHT;
import static ui.EscapeSequences.BLACK_PAWN;
import static ui.EscapeSequences.BLACK_QUEEN;
import static ui.EscapeSequences.BLACK_ROOK;
import static ui.EscapeSequences.EMPTY;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_MAGENTA;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;
import static ui.EscapeSequences.WHITE_BISHOP;
import static ui.EscapeSequences.WHITE_KING;
import static ui.EscapeSequences.WHITE_KNIGHT;
import static ui.EscapeSequences.WHITE_PAWN;
import static ui.EscapeSequences.WHITE_QUEEN;
import static ui.EscapeSequences.WHITE_ROOK;

public class Board {
    public ChessBoard chessBoard;

    public Board() {
        this.chessBoard = new ChessBoard();
        chessBoard.resetBoard();
    }

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public static void drawHeaders(PrintStream out, boolean  isWhite) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_YELLOW);
        String[] headers = {"a", "b", "c", "d", "e", "f", "g", "h"};
        out.print(" ");
        out.print(EMPTY);
        var header = Arrays.asList(headers);
        for (String h: isWhite == true ? header : header.reversed()) {
            out.print(" ");
            out.print(h);
            out.print(" ");
        }
        out.print(" ");
        out.print(EMPTY);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    public void drawBoard(PrintStream out, boolean isWhite, Collection<ChessMove> moves) {
        drawHeaders(out, isWhite);
        if (isWhite == false) {
            for (int boardRow = 1 ; boardRow <= BOARD_SIZE_IN_SQUARES ; ++boardRow) {
                drawRow(out,boardRow,isWhite, moves);
            }
        } else {
            for (int boardRow = BOARD_SIZE_IN_SQUARES ; boardRow >= 1 ; --boardRow) {
                drawRow(out,boardRow,isWhite, moves);
            }
        }
        drawHeaders(out, isWhite);
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private void drawRow(PrintStream out, int row, boolean isWhite, Collection<ChessMove> moves) {
        out.print(SET_BG_COLOR_MAGENTA + SET_TEXT_COLOR_YELLOW + row + EMPTY + RESET_BG_COLOR);

        Collection<ChessPosition> poses;
        if (moves!= null) {
            poses = moves.stream()
                        .map(ChessMove::getEndPosition)
                        .collect(Collectors.toSet());
        } else {
            poses = null;
        }
        if (isWhite) {
            for (int col = 1; col <= 8; ++col) {
                ChessPosition currentPos = new ChessPosition(row, col);

                if (poses != null && poses.contains(currentPos)) {
                    out.print(EscapeSequences.SET_BG_COLOR_BLUE);
                } else if ((row + col) % 2 == 0) {
                    out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }

                ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
                out.print(getPiece(piece) + SET_TEXT_COLOR_YELLOW );
            }
        } else {
            for (int col = 8; col >= 1; --col) {
                ChessPosition currentPos = new ChessPosition(row, col);

                if (poses != null && poses.contains(currentPos)) {
                    out.print(EscapeSequences.SET_BG_COLOR_BLUE);
                } else if ((row + col) % 2 == 0) {
                    out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }

                ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
                out.print(getPiece(piece) + SET_TEXT_COLOR_YELLOW );
            }
        }

        out.print(SET_BG_COLOR_MAGENTA + SET_TEXT_COLOR_YELLOW + EMPTY + row + RESET_BG_COLOR);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static String getPiece(ChessPiece piece) {
        if (piece == null) return EMPTY;
        boolean isWhite = (piece.getTeamColor() == ChessGame.TeamColor.WHITE);
        return switch (piece.getPieceType()) {
            case PAWN -> isWhite ? WHITE_PAWN : BLACK_PAWN; 
            case ROOK -> isWhite ? WHITE_ROOK : BLACK_ROOK; 
            case KNIGHT -> isWhite ? WHITE_KNIGHT : BLACK_KNIGHT; 
            case BISHOP -> isWhite ? WHITE_BISHOP : BLACK_BISHOP; 
            case QUEEN -> isWhite ? WHITE_QUEEN : BLACK_QUEEN; 
            case KING -> isWhite ? WHITE_KING : BLACK_KING;           
        };
    }
}
