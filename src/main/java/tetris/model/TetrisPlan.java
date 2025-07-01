package tetris.model;

import java.util.Arrays;
import java.util.Random;

/**
 * A class used to store the game state of the tetris game. State is stored in
 * the int[][] board array where: -1 means empty, 0-6 means a piece. Described
 * further in PieceFactory.
 *
 * @author davidwolf
 */
public class TetrisPlan {

    public static final int PIECE_COUNT = 7;
    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;

    private int[][] board;
    private Piece piece;
    private int xStart;
    private int yStart;

    private int next;
    private int hold;
    private boolean usedHold;
    private boolean playing;

    public TetrisPlan() {
        board = new int[WIDTH][HEIGHT];
        for (int x = 0; x < board.length; x++) {
            Arrays.fill(board[x], -1);
        }
        hold = -1;
        playing = true;
    }

    public int[][] getBoard() {
        return board;
    }

    public int[][] getCurrentPiece() {
        // Make empty board
        int[][] result = new int[WIDTH][HEIGHT];
        for (int i = 0; i < result.length; i++) {
            Arrays.fill(result[i], -1);
        }
        // Put piece on board
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

    public int[][] getPieceShadow() {
        // Make empty board
        int[][] result = new int[WIDTH][HEIGHT];
        for (int i = 0; i < result.length; i++) {
            Arrays.fill(result[i], -1);
        }
        // Find lowest placement
        int temp = yStart;
        while (move(0, 1)) {
        }
        // Put piece on board
        for (int x = 0; x < piece.getSize(); x++) {
            for (int y = 0; y < piece.getSize(); y++) {
                int value = piece.getValueAt(x, y);
                if (value == -1) {
                    continue;
                }
                result[x + xStart][y + yStart] = value;
            }
        }
        yStart = temp;
        return result;
    }

    public int[][] getHoldPiece() {
        if (hold == -1) {
            return PieceFactory.createEmptyPiece().shape;
        }
        return PieceFactory.createPiece(hold).shape;
    }

    public int[][] getNextPiece() {
        return PieceFactory.createPiece(next).shape;
    }

    public boolean hold() {
        if (usedHold) {
            return false;
        }
        if (hold == -1) {
            hold = piece.getIndex();
            piece = null;
            newPiece();
            newNextPiece();
            usedHold = true;
            return true;
        }
        int temp = next;
        next = hold;
        hold = piece.getIndex();
        piece = null;
        newPiece();
        next = temp;
        usedHold = true;
        return true;
    }

    public Piece getPiece() {
        return piece;
    }

    public void newPiece() {
        usedHold = false;
        if (piece != null) {
            throw new RuntimeException("Tried to make a new piece but a piece already exists");
        }
        piece = PieceFactory.createPiece(next);
        next = -1; // Impossible value to make sure the same piece isn't used twice in a row

        xStart = WIDTH / 2 - piece.getSize() / 2;
        yStart = 0;
        while (move(0, -1)) {
        }
        if (!isLegal(0, 0)) {
            this.playing = false;
        }
    }

    public void newNextPiece() {
        Random r = new Random();
        next = r.nextInt(PIECE_COUNT);
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
        this.piece = null; // Ensures NullPointerException will be thrown if code tries to use the non-existent piece

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
            }
        }
        board = newBoard;
    }

    public boolean isPlaying() {
        return playing;
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
        return x < 0 || y < 0 || x >= board.length || y >= board[0].length;
    }
}
