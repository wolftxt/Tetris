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
    }

    public Piece getPiece() {
        return piece;
    }

    public int[][] getBoard() {
        // Clone board array
        int[][] result = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            result[i] = board[i].clone();
        }

        // Place piece on board
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

    public void newPiece() {
        if (piece != null) {
            throw new RuntimeException("Tried to make a new piece but a piece already exists");
        }
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
        next = -1; // Impossible value to make sure the same piece isn't used twice in a row

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

    public boolean move(int x, int y) {
        if (!isLegal(x, y)) {
            return false;
        }
        xStart += x;
        yStart += y;
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
        this.piece = null; // Ensures NullPointerException will be trown if code tries to use the non-existant piece

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
