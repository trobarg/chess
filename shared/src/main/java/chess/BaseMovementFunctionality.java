package chess;

import java.util.Set;

public abstract class BaseMovementFunctionality implements MovementRule {
    public void calculateMoves(ChessBoard board, ChessPosition position, int rowInc, int colInc, Set<ChessMove> moves, boolean allowDistance) {
        if (allowDistance) {
            while(0 < position.getRow()+rowInc && position.getRow()+rowInc < 9 && 0 < position.getColumn()+colInc && position.getColumn()+colInc < 9) {
                moves.add(new ChessMove(position, new ChessPosition(position.getRow()+rowInc, position.getColumn()+colInc), null));
            }
        }
        else {
            if (0 < position.getRow()+rowInc && position.getRow()+rowInc < 9 && 0 < position.getColumn()+colInc && position.getColumn()+colInc < 9) {
                moves.add(new ChessMove(position, new ChessPosition(position.getRow()+rowInc, position.getColumn()+colInc), null));
            }
        }

    }
    public abstract Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}
