package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private final ChessBoard board = new ChessBoard();
    private TeamColor currentTeamTurn = TeamColor.WHITE;
    private boolean gameOver = false;
    private boolean whiteCouldRightCastle = true;
    private boolean whiteCouldLeftCastle = true;
    private boolean blackCouldRightCastle = true;
    private boolean blackCouldLeftCastle = true;
    private boolean whiteCouldEnPassant = false;
    private boolean blackCouldEnPassant = false;
    private int enPassantColumn;

    public ChessGame() {
        board.resetBoard();
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
            ChessPiece piece = board.getPiece(startPosition);
            Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
            Collection<ChessMove> validMoves = new HashSet<>();
            for (ChessMove potentialMove : potentialMoves) {
                ChessPiece prevPiece = board.getPiece(potentialMove.getEndPosition());
                board.removePiece(potentialMove.getStartPosition());
                board.addPiece(potentialMove.getEndPosition(), piece);
                if (!isInCheck(piece.getTeamColor())) {
                    validMoves.add(potentialMove);
                }
                board.addPiece(potentialMove.getStartPosition(), piece);
                board.addPiece(potentialMove.getEndPosition(), prevPiece);
            }
            return validMoves;
        }
        else {
            return null;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) != null) {
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if (piece.getTeamColor() != currentTeamTurn) {
                throw new InvalidMoveException();
            }
            boolean matchingMove = false;
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
            for (ChessMove validMove : validMoves) {
                if (validMove.equals(move)) {
                    board.removePiece(move.getStartPosition());
                    if (move.getPromotionPiece() == null) {
                        board.addPiece(move.getEndPosition(), piece);
                    }
                    else {
                        board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
                    }
                    matchingMove = true;
                    if (getTeamTurn() == TeamColor.WHITE) {
                        setTeamTurn(TeamColor.BLACK);
                    }
                    else {
                        setTeamTurn(TeamColor.WHITE);
                    }
                    if (isInCheckmate(currentTeamTurn) || isInStalemate(currentTeamTurn)) {
                        gameOver = true;
                    }
                    break;
                }
            }
            if (!matchingMove) {
                throw new InvalidMoveException();
            }
        }
        else {
            throw new InvalidMoveException();
        }
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
                ChessPosition currentPosition = new ChessPosition(i, j);
                if (board.getPiece(currentPosition) != null &&
                        board.getPiece(currentPosition).getPieceType().equals(ChessPiece.PieceType.KING) &&
                        board.getPiece(currentPosition).getTeamColor().equals(teamColor)) {
                    kingPosition = currentPosition;
                    break;
                }
            }
        }
        if (kingPosition == null) {
            return false;
        }
        boolean inCheck = false;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(currentPosition);
                if (isOpponentPiece(piece, teamColor) && threatensKing(piece, board, currentPosition, kingPosition)) {
                    inCheck = true;
                    break;
                }
            }
        }
        return inCheck;
    }
    private boolean isOpponentPiece(ChessPiece piece, TeamColor teamColor) {
        return piece != null && piece.getTeamColor() != teamColor;
    }

    private boolean threatensKing(ChessPiece piece, ChessBoard board, ChessPosition position, ChessPosition kingPosition) {
        for (ChessMove move : piece.pieceMoves(board, position)) {
            if (move.getEndPosition().equals(kingPosition)) {
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
        else {
            return noMovesPossible(teamColor);
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        else {
            return noMovesPossible(teamColor);
        }
    }

    /**
     * Determines if the given team has no possible moves to make
     *
     * @param teamColor which team to check
     * @return True if the specified team has no moves possible
     */
    private boolean noMovesPossible(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                if (board.getPiece(currentPosition) != null &&
                        board.getPiece(currentPosition).getTeamColor() == teamColor) {
                    if (!validMoves(currentPosition).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                this.board.addPiece(currentPosition, board.getPiece(currentPosition));
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public boolean getGameOver() {
        return gameOver;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    @Override
    public String toString() {
        return board.toString() + currentTeamTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTeamTurn == chessGame.currentTeamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTeamTurn);
    }
}
