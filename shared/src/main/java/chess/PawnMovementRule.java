package chess;

import java.util.HashSet;
import java.util.Set;

public class PawnMovementRule extends BaseMovementFunctionality{
    @Override
    public Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> moves = new HashSet<>();
        boolean promotionMove = false;
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (position.getRow() == 2 &&
                    board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn())) == null &&
                    board.getPiece(new ChessPosition(position.getRow() + 2, position.getColumn())) == null) {
                calculateMoves(board, position, 2, 0, moves, false, promotionMove);
            }
            else if (position.getRow() == 7) {
                promotionMove = true;
            }
            if (position.getColumn() > 1 &&
                    board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn() - 1)) != null &&
                    board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.BLACK) {
                calculateMoves(board, position, 1, -1, moves, false, promotionMove);
            }
            if (position.getColumn() < 8 &&
                    board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn() + 1)) != null &&
                    board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn() + 1 )).getTeamColor() == ChessGame.TeamColor.BLACK) {
                calculateMoves(board, position, 1, 1, moves, false, promotionMove);
            }
            if (board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn())) == null) {
                calculateMoves(board, position, 1, 0, moves, false, promotionMove);
            }
        }
        else if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (position.getRow() == 7 &&
                    board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn())) == null &&
                    board.getPiece(new ChessPosition(position.getRow() - 2, position.getColumn())) == null) {
                calculateMoves(board, position, -2, 0, moves, false, promotionMove);
            }
            else if (position.getRow() == 2) {
                promotionMove = true;
            }
            if (position.getColumn() > 1 &&
                    board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn() - 1)) != null &&
                    board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.WHITE) {
                calculateMoves(board, position, -1, -1, moves, false, promotionMove);
            }
            if (position.getColumn() < 8 &&
                    board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn() + 1)) != null &&
                    board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn() + 1)).getTeamColor() == ChessGame.TeamColor.WHITE) {
                calculateMoves(board, position, -1, 1, moves, false, promotionMove);
            }
            if (board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn())) == null) {
                calculateMoves(board, position, -1, 0, moves, false, promotionMove);
            }
        }
        return moves;
    }
}
