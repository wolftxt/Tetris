package tetris;

import tetris.model.Piece;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class PieceTest {

    public PieceTest() {
    }

    @org.junit.Test
    public void testRotate() {
        int[][] original = {{-1, -1, -1, -1}, {6, 6, 6, 6}, {-1, -1, -1, -1}, {-1, -1, -1, -1}};
        Piece piece = new Piece(original, 6);

        piece.rotate();
        int[][] result = {{-1, -1, 6, -1}, {-1, -1, 6, -1}, {-1, -1, 6, -1}, {-1, -1, 6, -1}};
        testEquality(piece.shape, result);

        piece.rotate();
        result = new int[][]{{-1, -1, -1, -1}, {-1, -1, -1, -1}, {6, 6, 6, 6}, {-1, -1, -1, -1}};
        testEquality(piece.shape, result);

        piece.rotate();
        result = new int[][]{{-1, 6, -1, -1}, {-1, 6, -1, -1}, {-1, 6, -1, -1}, {-1, 6, -1, -1}};
        testEquality(piece.shape, result);

        piece.rotate();
        testEquality(piece.shape, original);
    }

    private void testEquality(int[][] expected, int[][] result) {
        Assert.assertEquals("Arrays have different dimensions", expected.length, result.length);
        Assert.assertEquals("Arrays have different dimensions", expected[0].length, result[0].length);
        for (int i = 0; i < result.length; i++) {
            Assert.assertArrayEquals(expected[i], result[i]);
        }
    }
}
