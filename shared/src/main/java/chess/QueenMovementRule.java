package chess;

import java.util.Set;
import java.util.HashSet;

public class QueenMovementRule extends BaseMovementFunctionality {
    @Override
    public Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> moves = new HashSet<ChessMove>();
        calculateMoves(board, position, -1, -1, moves, true);
        calculateMoves(board, position, 0, -1, moves, true);
        calculateMoves(board, position, 1, -1, moves, true);
        calculateMoves(board, position, 1, 0, moves, true);
        calculateMoves(board, position, 1, 1, moves, true);
        calculateMoves(board, position, 0, 1, moves, true);
        calculateMoves(board, position, -1, 1, moves, true);
        calculateMoves(board, position, -1, 0, moves, true);
        return moves;
    }
}
