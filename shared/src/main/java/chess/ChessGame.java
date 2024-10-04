package chess;

import java.util.Collection;
import java.util.Set;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
private final ChessBoard board = new ChessBoard();
private TeamColor currentTeamTurn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) != null) {
            return board.getPiece(startPosition).pieceMoves(board, startPosition);
        }
        else return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (board.getPiece(new ChessPosition(i,j)) != null &&
                board.getPiece(new ChessPosition(i, j)).getPieceType().equals(ChessPiece.PieceType.KING) &&
                board.getPiece(new ChessPosition(i, j)).getTeamColor().equals(teamColor)) {
                    kingPosition = new ChessPosition(i,j);
                    break;
                }
            }
        }
        if (kingPosition == null) {
            return false;
        }
        boolean check = false;
        for (int i = 1; i < 9; i++) {
            for (int j=1; j < 9; j++) {
                if (board.getPiece(new ChessPosition(i,j)) != null &&
                    board.getPiece(new ChessPosition(i,j)).getTeamColor() != teamColor) {
                    Set<ChessMove> pieceMoves = (Set<ChessMove>) board.getPiece(new ChessPosition(i,j)).pieceMoves(board, new ChessPosition(i,j));
                    for (ChessMove move : pieceMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            check = true;
                            break; //stops as soon as first threatening piece is found
                        }
                    }
                }
            }
        }
        return check;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean check = isInCheck(teamColor);

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean check = isInCheck(teamColor);

    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) { //perform deep copy? shallow copy?
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
    @Override
    public String toString() {
        return board.toString() + " " + currentTeamTurn;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else if (!(o instanceof ChessGame)) return false;
        else {
            ChessGame other = (ChessGame) o;
            return this.board.equals(other.board) && this.currentTeamTurn.equals(other.currentTeamTurn);
        }
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + board.hashCode();
        result = prime * result + currentTeamTurn.hashCode();
        return result;
    }
}
