package tetris.model;

/**
 * Factory pattern class that allows the program to get each piece with just a
 * int where: 0 = square piece, 1 = L piece, 2 = J piece, 3 = T piece, 4 = S
 * piece, 5 = Z piece, 6 = line piece
 *
 * @author davidwolf
 */
public class PieceFactory {

    public static Piece createPiece(int index) {
        switch (index) {
            case -1 -> {
                return PieceFactory.createEmptyPiece();
            }
            case 0 -> {
                return PieceFactory.createSquarePiece();
            }
            case 1 -> {
                return PieceFactory.createLPiece();
            }
            case 2 -> {
                return PieceFactory.createJPiece();
            }
            case 3 -> {
                return PieceFactory.createTPiece();
            }
            case 4 -> {
                return PieceFactory.createSPiece();
            }
            case 5 -> {
                return PieceFactory.createZPiece();
            }
            case 6 -> {
                return PieceFactory.createLinePiece();
            }
            default ->
                throw new RuntimeException("Invalid next piece number");
        }
    }

    public static Piece createEmptyPiece() {
        int[][] shape = {{-1, -1}, {-1, -1}};
        return new Piece(shape, -1);
    }

    public static Piece createSquarePiece() {
        int[][] shape = {{0, 0}, {0, 0}};
        return new Piece(shape, 0);
    }

    public static Piece createLPiece() {
        int[][] shape = {{-1, 1, -1}, {-1, 1, -1}, {1, 1, -1}};
        return new Piece(shape, 1);
    }

    public static Piece createJPiece() {
        int[][] shape = {{2, 2, -1}, {-1, 2, -1}, {-1, 2, -1}};
        return new Piece(shape, 2);
    }

    public static Piece createTPiece() {
        int[][] shape = {{-1, 3, -1}, {3, 3, -1}, {-1, 3, -1}};
        return new Piece(shape, 3);
    }

    public static Piece createSPiece() {
        int[][] shape = {{4, -1, -1}, {4, 4, -1}, {-1, 4, -1}};
        return new Piece(shape, 4);
    }

    public static Piece createZPiece() {
        int[][] shape = {{-1, 5, -1}, {5, 5, -1}, {5, -1, -1}};
        return new Piece(shape, 5);
    }

    public static Piece createLinePiece() {
        int[][] shape = {{-1, 6, -1, -1}, {-1, 6, -1, -1}, {-1, 6, -1, -1}, {-1, 6, -1, -1}};
        return new Piece(shape, 6);
    }
}
