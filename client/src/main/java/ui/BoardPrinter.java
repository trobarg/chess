package ui;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class BoardPrinter {
    private ChessGame game;

    public BoardPrinter(ChessGame game) {
        this.game = game;
    }

    public void printBoard(ChessGame.TeamColor color, ChessPosition selectedPos) {
        StringBuilder output = new StringBuilder();
        output.append(SET_TEXT_BOLD);

        Collection<ChessMove> possibleMoves = selectedPos != null ? game.validMoves(selectedPos) : null;
        HashSet<ChessPosition> possibleSquares = HashSet.newHashSet(possibleMoves != null ? possibleMoves.size() : 0);
        if (possibleMoves != null) {
            for (ChessMove move : possibleMoves) {
                possibleSquares.add(move.getEndPosition());
            }
        }
        boolean reversed = color == ChessGame.TeamColor.BLACK;
        output.append(exteriorRow(reversed));

        for (int i = 8; i > 0; i--) {
            int row = !reversed ? i : (i * -1) + 9;
            output.append(boardRow(row, reversed, selectedPos, possibleSquares));
        }

        output.append(exteriorRow(reversed));
        out.println(output);
    }

    private String exteriorRow(boolean reversed) {
        StringBuilder output = new StringBuilder();
        output.append(SET_BG_COLOR_BLACK);
        output.append(SET_TEXT_COLOR_BLUE);
        output.append(!reversed ? "    a  b  c  d  e  f  g  h    " : "    h  g  f  e  d  c  b  a    ");
        output.append(RESET_BG_COLOR);
        output.append(RESET_TEXT_COLOR);
        output.append("\n");
        return output.toString();
    }

    private String boardRow(int row, boolean reversed, ChessPosition startingSquare, HashSet<ChessPosition> highlightedSquares) {
        StringBuilder output = new StringBuilder();
        output.append(SET_BG_COLOR_BLACK);
        output.append(SET_TEXT_COLOR_BLUE);
        output.append(" %d ".formatted(row));

        for (int i = 1; i < 9; i++) {
            int column = !reversed ? i : (i * -1) + 9;
            output.append(squareColor(row, column, startingSquare, highlightedSquares));
            output.append(piece(row, column));
        }

        output.append(SET_BG_COLOR_BLACK);
        output.append(SET_TEXT_COLOR_BLUE);
        output.append(" %d ".formatted(row));
        output.append(RESET_BG_COLOR);
        output.append(RESET_TEXT_COLOR);

        output.append("\n");
        return output.toString();
    }

    private String squareColor(int row, int column, ChessPosition startingSquare, HashSet<ChessPosition> highlightedSquares) {
        ChessPosition square = new ChessPosition(row, column);
        if (square.equals(startingSquare)) {
            return SET_BG_COLOR_BLUE;
        }
        else if (highlightedSquares.contains(square)) {
            return SET_BG_COLOR_DARK_GREEN;
        }
        else if (row % 2 == 0) {
            if (column % 2 == 0) {
                return SET_BG_COLOR_RED;
            } else {
                return SET_BG_COLOR_LIGHT_GREY;
            }
        } else {
            if (column % 2 == 0) {
                return SET_BG_COLOR_LIGHT_GREY;
            } else {
                return SET_BG_COLOR_RED;
            }
        }
    }

    private String piece(int row, int column) {
        StringBuilder output = new StringBuilder();
        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = game.getBoard().getPiece(position);

        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                output.append(SET_TEXT_COLOR_WHITE);
            } else {
                output.append(SET_TEXT_COLOR_BLACK);
            }

            switch (piece.getPieceType()) {
                case QUEEN -> output.append(" Q ");
                case KING -> output.append(" K ");
                case BISHOP -> output.append(" B ");
                case KNIGHT -> output.append(" N ");
                case ROOK -> output.append(" R ");
                case PAWN -> output.append(" P ");
            }
        } else {
            output.append("   ");
        }

        return output.toString();
    }
}
