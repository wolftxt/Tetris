package tetris.model;

public class PieceFactory {

    public static Piece createSquarePiece() {
        int[][] shape = {{0, 0}, {0, 0}};
        return new Piece(shape);
    }

    public static Piece createLPiece() {
        int[][] shape = {{1, -1, -1}, {1, 1, 1}, {-1, -1, -1}};
        return new Piece(shape);
    }

    public static Piece createJPiece() {
        int[][] shape = {{-1, -1, 2}, {2, 2, 2}, {-1, -1, -1}};
        return new Piece(shape);
    }

    public static Piece createTPiece() {
        int[][] shape = {{-1, 3, -1}, {3, 3, 3}, {-1, -1, -1}};
        return new Piece(shape);
    }

    public static Piece createSPiece() {
        int[][] shape = {{-1, 4, 4}, {4, 4, -1}, {-1, -1, -1}};
        return new Piece(shape);
    }

    public static Piece createZPiece() {
        int[][] shape = {{5, 5, -1}, {-1, 5, 5}, {-1, -1, -1}};
        return new Piece(shape);
    }

    public static Piece createLinePiece() {
        int[][] shape = {{-1, -1, -1, -1}, {6, 6, 6, 6}, {-1, -1, -1, -1}, {-1, -1, -1, -1}};
        return new Piece(shape);
    }
}
