package chess;

import java.util.HashSet;
import java.util.Set;

public class BishopMovementRule extends BaseMovementFunctionality {
    @Override
    public Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> moves = new HashSet<>();
        calculateMoves(board, position, -1, -1, moves, true, false);
        calculateMoves(board, position, 1, -1, moves, true, false);
        calculateMoves(board, position, -1, 1, moves, true, false);
        calculateMoves(board, position, 1, 1, moves, true, false);
        return moves;
    }
}
