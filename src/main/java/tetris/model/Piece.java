package tetris.model;

public class Piece {

    public int[][] shape;
    private int index;

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

    public int getIndex() {
        return index;
    }

    public int getValueAt(int x, int y) {
        return shape[x][y];
    }

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
    }
}
