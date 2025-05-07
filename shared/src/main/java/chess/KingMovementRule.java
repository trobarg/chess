package chess;

import java.util.HashSet;
import java.util.Set;

public class KingMovementRule extends BaseMovementFunctionality{
    @Override
    public Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> moves = new HashSet<ChessMove>();
        calculateMoves(board, position, -1, -1, moves, false, false);
        calculateMoves(board, position, 0, -1, moves, false, false);
        calculateMoves(board, position, 1, -1, moves, false, false);
        calculateMoves(board, position, 1, 0, moves, false, false);
        calculateMoves(board, position, 1, 1, moves, false, false);
        calculateMoves(board, position, 0, 1, moves, false, false);
        calculateMoves(board, position, -1, 1, moves, false, false);
        calculateMoves(board, position, -1, 0, moves, false, false);
        return moves;
    }
}
