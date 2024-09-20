package chess;

import java.util.Set;

public abstract class BaseMovementFunctionality implements MovementRule {
    public void calculateMoves(ChessBoard board, ChessPosition position, int rowInc, int colInc, Set<ChessMove> moves, boolean allowDistance) {
        int rowIncTotal = rowInc;
        int colIncTotal = colInc;
        ChessPosition newPosition = new ChessPosition(position.getRow() + rowIncTotal, position.getColumn() + colIncTotal);
        while (0 < newPosition.getRow() && newPosition.getRow() < 9 && 0 < newPosition.getColumn() && newPosition.getColumn() < 9) {
            if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() == board.getPiece(position).getTeamColor()) {
                break;
            }
            else {
                moves.add(new ChessMove(position, newPosition, null));
            }
            if ((board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != board.getPiece(position).getTeamColor()) || !allowDistance) {
                break;
            }
            else {
                rowIncTotal += rowInc;
                colIncTotal += colInc;
                newPosition = new ChessPosition(position.getRow() + rowIncTotal, position.getColumn() + colIncTotal);
            }
        }
    }
    public abstract Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}
