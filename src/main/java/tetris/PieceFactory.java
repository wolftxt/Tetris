package tetris;

public class PieceFactory {

    public static Piece createSquarePiece() {
        boolean[][] shape = {{true, true}, {true, true}};
        return new Piece(shape);
    }

    public static Piece createLPiece() {
        boolean[][] shape = {{true, false, false}, {true, true, true}, {false, false, false}};
        return new Piece(shape);
    }

    public static Piece createJPiece() {
        boolean[][] shape = {{false, false, true}, {true, true, true}, {false, false, false}};
        return new Piece(shape);
    }

    public static Piece createTPiece() {
        boolean[][] shape = {{false, true, false}, {true, true, true}, {false, false, false}};
        return new Piece(shape);
    }

}
