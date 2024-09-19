package chess;

import java.util.Set;

public interface MovementRule {
    Set<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}
