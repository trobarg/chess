package chess;

import java.util.ArrayList;

public abstract class BaseMovementRule implements MovementRule {
    public void calculateMoves() {

    }
    public abstract ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}
