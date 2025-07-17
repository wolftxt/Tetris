package tetris.model;

import lombok.Data;

/**
 * A class representing the current falling piece. The piece is represented as
 * int[][] of square size where 2 <= n <= 4. The number in int represents the
 * type of piece described in detail in PieceFactory.
 *
 * @author davidwolf
 */
@Data
public class Piece {

    public int[][] shape;
    private int index;
    
    /*
    0 = spawn state
    1 = state resulting from a clockwise rotation ("right") from spawn
    2 = state resulting from 2 successive rotations in either direction from spawn.
    3 = state resulting from a counter-clockwise ("left") rotation from spawn*/
    private int rotationState;

    private static final int[][][][] SRS3x3 = new int[4][4][][];
    private static final int[][][][] SRSI = new int[4][4][][];

    /**
     * Initialize SRS array according to the tetris wiki:
     * https://tetris.fandom.com/wiki/SRS#Basic_Rotation
     */
    static {
        // 90°
        SRS3x3[0][1] = new int[][]{{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}};
        SRS3x3[1][0] = new int[][]{{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}};
        SRS3x3[1][2] = new int[][]{{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}};
        SRS3x3[2][1] = new int[][]{{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}};
        SRS3x3[2][3] = new int[][]{{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}};
        SRS3x3[3][2] = new int[][]{{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}};
        SRS3x3[3][0] = new int[][]{{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}};
        SRS3x3[0][3] = new int[][]{{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}};
        // 180°
        SRS3x3[0][2] = new int[][]{{0, 0}, {1, 0}, {2, 0}, {1, 1}, {2, 1}, {-1, 0}, {-2, 0}, {-1, 1}, {-2, 1}, {0, -1}, {3, 0}, {-3, 0}};
        SRS3x3[1][3] = new int[][]{{0, 0}, {0, 1}, {0, 2}, {-1, 1}, {-1, 2}, {0, -1}, {0, -2}, {-1, -1}, {-1, -2}, {1, 0}, {0, 3}, {0, -3}};
        SRS3x3[2][0] = new int[][]{{0, 0}, {-1, 0}, {-2, 0}, {-1, -1}, {-2, -1}, {1, 0}, {2, 0}, {1, -1}, {2, -1}, {0, 1}, {-3, 0}, {3, 0}};
        SRS3x3[3][1] = new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 1}, {1, 2}, {0, -1}, {0, -2}, {1, -1}, {1, -2}, {-1, 0}, {0, 3}, {0, -3}};

        // 90°
        SRSI[0][1] = new int[][]{{0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2}};
        SRSI[1][0] = new int[][]{{0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2}};
        SRSI[1][2] = new int[][]{{0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1}};
        SRSI[2][1] = new int[][]{{0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1}};
        SRSI[2][3] = new int[][]{{0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2}};
        SRSI[3][2] = new int[][]{{0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2}};
        SRSI[3][0] = new int[][]{{0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1}};
        SRSI[0][3] = new int[][]{{0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1}};
        // 180°
        SRSI[0][2] = new int[][]{{0, 0}, {-1, 0}, {-2, 0}, {1, 0}, {2, 0}, {0, 1}};
        SRSI[1][3] = new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, -1}, {0, -2}, {-1, 0}};
        SRSI[2][0] = new int[][]{{0, 0}, {1, 0}, {2, 0}, {-1, 0}, {-2, 0}, {0, -1}};
        SRSI[3][1] = new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, -1}, {0, -2}, {1, 0}};
    }

    public Piece(int[][] shape, int index) {
        if (shape.length != shape[0].length) {
            throw new RuntimeException("Shape array must be a square matrix");
        }
        this.shape = shape;
        this.index = index;
    }

    public int getSize() {
        return shape.length;
    }

    public int getValueAt(int x, int y) {
        return shape[x][y];
    }

    /**
     * Rotates piece counterclockwise by swapping rows and transposing the
     * matrix. Equivalent to this leetcode problem:
     * https://leetcode.com/problems/rotate-image/description/
     */
    public void rotate() {
        for (int x = 0; x < shape.length / 2; x++) {
            int ndIndex = shape.length - x - 1;
            int[] temp = shape[x];
            shape[x] = shape[ndIndex];
            shape[ndIndex] = temp;
        }
        for (int x = 0; x < shape.length; x++) {
            for (int y = 0; y < x; y++) {
                int temp = shape[x][y];
                shape[x][y] = shape[y][x];
                shape[y][x] = temp;
            }
        }
        rotationState--;
        if (rotationState < 0) {
            rotationState += 4;
        }
    }

    public boolean rotateSRS(int rotateTimes, TetrisPlan plan) {
        if (shape.length == 2) { // O Piece
            return false;
        }
        int originalState = rotationState;
        for (int i = 0; i < rotateTimes; i++) {
            rotate();
        }
        // length 4 is I piece
        int[][][][] SRS = shape.length == 4 ? SRSI : SRS3x3;
        for (int[] test : SRS[originalState][rotationState]) {
            if (plan.move(test[0], test[1])) {
                return true;
            }
        }
        for (int i = 0; i < 4 - rotateTimes; i++) {
            plan.getPiece().rotate();
        }
        return false;
    }
}
