package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionType;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionType = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionType;
    }

    @Override
    public String toString() {
        if (promotionType != null) {
            return startPosition.toString() + " to " + endPosition.toString() + " " + promotionType;
        }
        else {
            return startPosition.toString() + " to " + endPosition.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (this == o) return true;
        else {
            boolean promotionMatch = false;
            ChessMove other = (ChessMove) o;
            if (promotionType == null || other.promotionType == null) {
                if (promotionType == null && other.promotionType == null) {
                    promotionMatch = true;
                }
            } else if (promotionType.equals(other.promotionType)) {
                promotionMatch = true;
            }
            return startPosition.equals(other.startPosition) && endPosition.equals(other.endPosition)
                    && promotionMatch;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((startPosition == null) ? 0 : startPosition.hashCode());
        result = prime * result + ((endPosition == null) ? 0 : endPosition.hashCode());
        result = prime * result + ((promotionType == null) ? 0 : promotionType.hashCode());
        return result;
    }
}
