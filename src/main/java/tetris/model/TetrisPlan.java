package tetris.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import lombok.Data;
import tetris.settings.GameSettings;

/**
 * A class used to store the game state of the tetris game. State is stored in
 * the int[][] board array where: -1 means empty, 0-6 means a piece. Described
 * further in PieceFactory.
 *
 * @author davidwolf
 */
@Data
public class TetrisPlan {

    public static final int PIECE_COUNT = 7;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 21;

    private int[][] board;
    private int lineClearCount;
    private Piece piece;
    private int xStart;
    private int yStart;

    private List<Integer> next;
    private int hold;
    private boolean usedHold;
    private boolean playing;

    public TetrisPlan() {
        board = new int[WIDTH][HEIGHT];
        next = new LinkedList();
        for (int x = 0; x < board.length; x++) {
            Arrays.fill(board[x], -1);
        }
        lineClearCount = 0;
        hold = -1;
        playing = true;
    }

    public boolean hold() {
        if (!playing || usedHold) {
            return false;
        }
        if (hold == -1) {
            hold = piece.getIndex();
            newPiece();
            usedHold = true;
            return true;
        }
        int temp = hold;
        hold = piece.getIndex();
        newPiece(temp);
        usedHold = true;
        return true;
    }

    public boolean newPiece() {
        if (next.size() - 1 <= PIECE_COUNT) {
            newNextPieces();
        }
        return newPiece(next.removeFirst());
    }

    public boolean newPiece(int nextPiece) {
        usedHold = false;
        piece = PieceFactory.createPiece(nextPiece);

        xStart = WIDTH / 2 - (piece.getSize() + 1) / 2;
        yStart = 0; // DO NOT REMOVE, otherwise the new piece will bump into the old one in the next condition and the piece wont fit in the board.
        while (move(0, -1)) {
        }
        yStart += HEIGHT - 20;
        if (!isLegal(0, 0)) {
            this.playing = false;
        }
        return playing;
    }

    public void newNextPieces() {
        GameSettings gs = GameSettings.getInstance();
        if (gs.SEVEN_BAG) {
            // 7 bag piece system
            List<Integer> add = new LinkedList();
            for (int i = 0; i < PIECE_COUNT; i++) {
                add.add(i);
            }
            Collections.shuffle(add);
            next.addAll(add);
        } else {
            // Completely random pieces
            for (int i = 0; i < PIECE_COUNT; i++) {
                Random r = new Random();
                next.add(r.nextInt(PIECE_COUNT));
            }
        }
    }

    public boolean move(int x, int y) {
        if (!isLegal(x, y)) {
            return false;
        }
        xStart += x;
        yStart += y;
        return true;
    }

    /**
     * Method used to handle placing pieces and clearing lines
     */
    public void placePiece() {
        for (int x = 0; x < piece.getSize(); x++) {
            for (int y = 0; y < piece.getSize(); y++) {
                int value = piece.getValueAt(x, y);
                if (value == -1) {
                    continue;
                }
                board[x + xStart][y + yStart] = value;
            }
        }

        // clear lines
        int[][] newBoard = new int[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            Arrays.fill(newBoard[x], -1);
        }
        int lineIndex = HEIGHT - 1;
        for (int y = HEIGHT - 1; y >= 0; y--) {
            boolean clear = true;
            for (int x = 0; x < WIDTH; x++) {
                clear = clear && board[x][y] != -1;
            }
            if (!clear) {
                for (int x = 0; x < WIDTH; x++) {
                    newBoard[x][lineIndex] = board[x][y];
                }
                lineIndex--;
            } else {
                lineClearCount++;
            }
        }
        board = newBoard;
    }

    /**
     * Method that determines if the piece would overlap with placed pieces or
     * stick out of the board. In those cases it returns false.
     *
     * @param xOffset Offset on the x axis from the position determined by
     * xStart
     * @param yOffset Offset on the y axis from the position determined by
     * yStart
     * @return
     */
    public boolean isLegal(int xOffset, int yOffset) {
        int xStart = this.xStart + xOffset;
        int yStart = this.yStart + yOffset;
        for (int x = 0; x < piece.getSize(); x++) {
            for (int y = 0; y < piece.getSize(); y++) {
                int value = piece.getValueAt(x, y);
                if (value == -1) {
                    continue;
                }
                if (outOfBounds(x + xStart, y + yStart)) {
                    return false;
                }
                if (board[x + xStart][y + yStart] != -1) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean outOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT;
    }
}
