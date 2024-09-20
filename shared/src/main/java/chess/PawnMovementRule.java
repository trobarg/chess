package chess;

import java.util.Set;
import java.util.HashSet;

public class PawnMovementRule extends BaseMovementFunctionality {
    @Override
    public Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> moves = new HashSet<ChessMove>();
        boolean promotionMove = false;
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (position.getRow() == 2 && board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn())) != null) {

            }
            else if (position.getRow() == 7) {
                promotionMove = true;
            }
            if (board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.BLACK) {

            }
            if (board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn() + 1)).getTeamColor() == ChessGame.TeamColor.BLACK) {

            }
        }
        else if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (position.getRow() == 7 && board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn())) != null) {

            }
            else if (position.getRow() == 2) {
                promotionMove = true;
            }
            if (board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.WHITE) {

            }
            if (board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn() + 1)).getTeamColor() == ChessGame.TeamColor.WHITE) {

            }
        }


        return moves;
    }
}
