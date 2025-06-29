package tetris.view;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JComponent;
import tetris.controller.TetrisGame;
import tetris.model.TetrisPlan;

public class TetrisWidget extends JComponent {

    private static final Color BACKGROUND_COLOR = new Color(60, 60, 60);
    private static final Color GRID_COLOR = Color.BLACK;

    private TetrisGame game;
    private Color[] pieceColors;

    public TetrisWidget() {
        newGame();
    }

    public TetrisGame getGame() {
        return game;
    }

    public void newGame() {
        this.game = new TetrisGame(this, 2000);
        generateColors();
    }

    private void generateColors() {
        pieceColors = new Color[TetrisPlan.PIECE_COUNT];
        Random r = new Random();
        for (int i = 0; i < TetrisPlan.PIECE_COUNT; i++) {
            int red = r.nextInt(256);
            int green = r.nextInt(256);
            int blue = r.nextInt(256);
            pieceColors[i] = new Color(red, green, blue);
        }
    }

    private int getScaling(int width, int height) {
        int wScaling = this.getWidth() / width;
        int hScaling = this.getHeight() / height;
        return Math.min(wScaling, hScaling);
    }

    public int getXOffset(int width, int height) {
        int s = getScaling(width, height);
        int emptySpace = this.getWidth() - (width * s);
        return emptySpace / 2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int[][] board = game.getPlan().getBoard();

        int s = getScaling(board.length, board[0].length);
        int xOffset = getXOffset(board.length, board[0].length);

        // Draw background
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Draw board
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                g.setColor(GRID_COLOR);
                g.drawRect(xOffset + x * s, y * s, s, s);
                if (board[x][y] == -1) {
                    continue;
                }
                g.setColor(pieceColors[board[x][y]]);
                g.fillRect(xOffset + x * s, y * s, s, s);
            }
        }

        // Draw hold piece
        int[][] piece = game.getPlan().getHoldPiece();
        int xStart = xOffset - (piece.length + 1) * s; // + 1 for a 1 square wide space between board and hold piece
        drawPiece(piece, xStart, g, s);

        // Draw next piece
        piece = game.getPlan().getNextPiece();
        xStart = xOffset + (board.length + 1) * s; // + 1 for a 1 square wide space between board and hold piece
        drawPiece(piece, xStart, g, s);
    }

    private void drawPiece(int[][] piece, int xStart, Graphics g, int s) {
        for (int x = 0; x < piece.length; x++) {
            for (int y = 0; y < piece[0].length; y++) {
                g.setColor(GRID_COLOR);
                g.drawRect(xStart + x * s, y * s, s, s);
                if (piece[x][y] == -1) {
                    continue;
                }
                g.setColor(pieceColors[piece[x][y]]);
                g.fillRect(xStart + x * s, y * s, s, s);
            }
        }
    }

}
