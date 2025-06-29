package tetris.view;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JComponent;
import tetris.controller.TetrisGame;
import tetris.model.TetrisPlan;
import tetris.settings.UISettings;

public class TetrisWidget extends JComponent {

    private static final Color GRID_COLOR = Color.BLACK;

    private TetrisGame game;
    private Color[] pieceColors = {Color.YELLOW, Color.ORANGE, Color.BLUE, Color.MAGENTA, Color.RED, Color.GREEN, Color.CYAN};

    public TetrisWidget() {
        newGame(2000);
    }

    public TetrisGame getGame() {
        return game;
    }

    public void newGame(int timeToFall) {
        this.game = new TetrisGame(this, timeToFall);
        if (UISettings.getInstance().randomColors) {
            generateRandomColors();
        }
    }

    private void generateRandomColors() {
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
        UISettings settings = UISettings.getInstance();

        int[][] board = game.getPlan().getBoard();

        int s = getScaling(board.length, board[0].length);
        int xOffset = getXOffset(board.length, board[0].length);

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

        // Draw current piece
        int[][] piece = game.getPlan().getCurrentPiece();
        int xStart = xOffset;
        drawPiece(piece, xStart, g, s, null);

        // Draw piece shadow
        piece = game.getPlan().getPieceShadow();
        int index = game.getPlan().getPiece().getIndex();
        Color c = pieceColors[index];
        Color opaque = new Color(c.getRed(), c.getGreen(), c.getBlue(), settings.shadowPieceAlphaValue);
        drawPiece(piece, xStart, g, s, opaque);

        // Draw hold piece
        piece = game.getPlan().getHoldPiece();
        xStart = xOffset - (piece.length + 1) * s; // + 1 for a 1 square wide space between board and hold piece
        drawPiece(piece, xStart, g, s, null);

        // Draw next piece
        piece = game.getPlan().getNextPiece();
        xStart = xOffset + (board.length + 1) * s; // + 1 for a 1 square wide space between board and hold piece
        drawPiece(piece, xStart, g, s, null);
    }

    private void drawPiece(int[][] piece, int xStart, Graphics g, int s, Color pieceColor) {
        for (int x = 0; x < piece.length; x++) {
            for (int y = 0; y < piece[0].length; y++) {
                g.setColor(GRID_COLOR);
                g.drawRect(xStart + x * s, y * s, s, s);
                if (piece[x][y] == -1) {
                    continue;
                }
                if (pieceColor == null) {
                    pieceColor = pieceColors[piece[x][y]];
                }
                g.setColor(pieceColor);
                g.fillRect(xStart + x * s, y * s, s, s);
            }
        }
    }

}
