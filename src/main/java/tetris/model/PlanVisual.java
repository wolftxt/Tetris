package tetris.model;

import java.util.Arrays;

public class PlanVisual {

    public static int[][] getCurrentPiece(TetrisPlan plan) {
        // Make empty board
        int[][] result = new int[TetrisPlan.WIDTH][TetrisPlan.HEIGHT];
        for (int i = 0; i < result.length; i++) {
            Arrays.fill(result[i], -1);
        }
        // Put piece on board
        Piece piece = plan.getPiece();
        for (int x = 0; x < piece.getSize(); x++) {
            for (int y = 0; y < piece.getSize(); y++) {
                int value = piece.getValueAt(x, y);
                if (value == -1) {
                    continue;
                }
                result[x + plan.getxStart()][y + plan.getyStart()] = value;
            }
        }
        return result;
    }

    /**
     * Used by TetrisWidget to show where a piece would fall
     *
     * @return
     */
    public int[][] getPieceShadow(TetrisPlan plan) {
        // Make empty board
        int[][] result = new int[TetrisPlan.WIDTH][TetrisPlan.HEIGHT];
        for (int i = 0; i < result.length; i++) {
            Arrays.fill(result[i], -1);
        }
        // Find lowest placement
        int temp = plan.getyStart();
        while (plan.move(0, 1)) {
        }
        // Put piece on board
        Piece piece = plan.getPiece();
        for (int x = 0; x < piece.getSize(); x++) {
            for (int y = 0; y < piece.getSize(); y++) {
                int value = piece.getValueAt(x, y);
                if (value == -1) {
                    continue;
                }
                result[x + plan.getxStart()][y + plan.getyStart()] = value;
            }
        }
        plan.setyStart(temp);
        return result;
    }
}
