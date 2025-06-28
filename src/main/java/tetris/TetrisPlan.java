package tetris;

import java.util.Arrays;
import java.util.Random;

/**
 * A class used to store the game state of the tetris game. State is stored in
 * the int[][] board array where: -1 means empty, everything
 * else means occupied (more numbers for different colors)
 *
 * @author davidwolf
 */
public class TetrisPlan {

    public static final int PIECE_COUNT = 7;

    private int[][] board;
    private boolean[][] piece;
    private int next;
    private boolean playing;

    public TetrisPlan() {
        board = new int[10][20];
        for (int x = 0; x < board.length; x++) {
            Arrays.fill(board[x], -1);
        }
        playing = true;
    }

    public boolean newPiece() {
        throw new RuntimeException("NOT yet implemented");
    }

    public void newNextPiece() {
        Random r = new Random();
        next = r.nextInt(PIECE_COUNT);
    }

}
