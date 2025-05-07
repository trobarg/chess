package chess;

import java.util.HashSet;
import java.util.Set;

public class KnightMovementRule extends BaseMovementFunctionality{
    @Override
    public Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> moves = new HashSet<ChessMove>();
        calculateMoves(board, position, -2, -1, moves, false, false);
        calculateMoves(board, position, -1, -2, moves, false, false);
        calculateMoves(board, position, 1, -2, moves, false, false);
        calculateMoves(board, position, 2, -1, moves, false, false);
        calculateMoves(board, position, 2, 1, moves, false, false);
        calculateMoves(board, position, 1, 2, moves, false, false);
        calculateMoves(board, position, -1, 2, moves, false, false);
        calculateMoves(board, position, -2, 1, moves, false, false);
        return moves;
    }
}
