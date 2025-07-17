package tetris.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import tetris.settings.GameSettings;

/**
 * A class with static methods that take TetrisPlan as an argument used to get
 * useful info to be displayed
 *
 * @author davidwolf
 */
public class PlanVisual {

    public static List<int[][]> getNextPiece(TetrisPlan plan) {
        GameSettings gs = GameSettings.getInstance();
        List<Integer> next = plan.getNext().subList(0, gs.PREVIEW_PIECE_COUNT);

        List<int[][]> result = new LinkedList();
        for (int integer : next) {
            result.add(PieceFactory.createPiece(integer).shape);
        }
        return result;
    }

    public static int[][] getHoldPiece(TetrisPlan plan) {
        return PieceFactory.createPiece(plan.getHold()).shape;
    }

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
                result[x + plan.getXStart()][y + plan.getYStart()] = value;
            }
        }
        return result;
    }

    /**
     * Used by TetrisWidget to show where a piece would fall
     *
     * @return
     */
    public static int[][] getPieceShadow(TetrisPlan plan) {
        // Make empty board
        int[][] result = new int[TetrisPlan.WIDTH][TetrisPlan.HEIGHT];
        for (int i = 0; i < result.length; i++) {
            Arrays.fill(result[i], -1);
        }
        // Find lowest placement
        int yOffset = 0;
        while (plan.isLegal(0, yOffset + 1)) {
            yOffset++;
        }
        int xStart = plan.getXStart();
        int yStart = plan.getYStart() + yOffset;
        // Put piece on board
        Piece piece = plan.getPiece();
        for (int x = 0; x < piece.getSize(); x++) {
            for (int y = 0; y < piece.getSize(); y++) {
                int value = piece.getValueAt(x, y);
                if (value == -1) {
                    continue;
                }
                result[x + xStart][y + yStart] = value;
            }
        }
        return result;
    }
}
