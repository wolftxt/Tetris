package tetris.model;

import java.util.Arrays;
import java.util.Random;

/**
 * A class used to store the game state of the tetris game. State is stored in
 * the int[][] board array where: -1 means empty, everything else means occupied
 * (more numbers for different colors)
 *
 * @author davidwolf
 */
public class TetrisPlan {

    public static final int PIECE_COUNT = 7;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    private int[][] board;
    private Piece piece;
    private int xStart;
    private int yStart;

    private int next;
    private boolean playing;

    public TetrisPlan() {
        board = new int[10][20];
        for (int x = 0; x < board.length; x++) {
            Arrays.fill(board[x], -1);
        }
        playing = true;
        newNextPiece();
    }

    public void newPiece() {
        switch (next) {
            case 0 -> {
                piece = PieceFactory.createSquarePiece();
            }
            case 1 -> {
                piece = PieceFactory.createLPiece();
            }
            case 2 -> {
                piece = PieceFactory.createJPiece();
            }
            case 3 -> {
                piece = PieceFactory.createTPiece();
            }
            case 4 -> {
                piece = PieceFactory.createSPiece();
            }
            case 5 -> {
                piece = PieceFactory.createZPiece();
            }
            case 6 -> {
                piece = PieceFactory.createLinePiece();
            }
            default ->
                throw new RuntimeException("Invalid next piece number");
        }
        xStart = WIDTH / 2 - piece.getSize() / 2;
        yStart = 0;
        if (!isLegal(0, 0)) {
            this.playing = false;
        }
    }

    public void newNextPiece() {
        Random r = new Random();
        next = r.nextInt(PIECE_COUNT);
    }

    public boolean moveDown() {
        if (!isLegal(0, 1)) {
            return false;
        }
        yStart++;
        return true;
    }

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
        this.piece = null; // Ensures NullPointerException will be trown if something goes wrong
    }

    public boolean isPlaying() {
        return playing;
    }

    private boolean isLegal(int xOffset, int yOffset) {
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
