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

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionType = null;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionType) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionType = promotionType;
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
        return startPosition.toString() + " to " + endPosition.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else if (!(o instanceof ChessMove other)) return false;
        else {
            return this.startPosition.equals(other.startPosition) && this.endPosition.equals(other.endPosition);
        }
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.startPosition == null) ? 0 : this.startPosition.hashCode());
        result = prime * result + ((this.endPosition == null) ? 0 : this.endPosition.hashCode());
        result = prime * result + ((this.promotionType == null) ? 0 : this.promotionType.hashCode());
        return result;
    }
}
