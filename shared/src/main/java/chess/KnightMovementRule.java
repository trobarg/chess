package chess;

import java.util.Set;
import java.util.HashSet;

public class KnightMovementRule extends BaseMovementFunctionality {
    @Override
    public Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> moves = new HashSet<ChessMove>();
        calculateMoves(board, position, -2, -1, moves, false);
        calculateMoves(board, position, -1, -2, moves, false);
        calculateMoves(board, position, 1, -2, moves, false);
        calculateMoves(board, position, 2, -1, moves, false);
        calculateMoves(board, position, 2, 1, moves, false);
        calculateMoves(board, position, 1, 2, moves, false);
        calculateMoves(board, position, -1, 2, moves, false);
        calculateMoves(board, position, -2, 1, moves, false);
        return moves;
    }
}
