package chess;

import java.util.ArrayList;

public class PawnMovementRule implements PieceMovesCalculator{
    @Override
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        return new ArrayList<>();
    }
}
