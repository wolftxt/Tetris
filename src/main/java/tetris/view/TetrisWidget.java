package tetris.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;
import tetris.controller.TetrisGame;
import tetris.model.PlanVisual;
import tetris.model.TetrisPlan;
import tetris.settings.GameSettings;

/**
 * A JComponent covering the entire window and showing the board, hold piece and
 * next piece. Allows for both random and default colors.
 *
 * @author davidwolf
 */
public class TetrisWidget extends JComponent {

    private static final Color BG = new Color(60, 60, 60);
    private static final Color GRID_COLOR = Color.BLACK;
    private static final Color FONT_COLOR = Color.WHITE;

    private TetrisGame game;
    private Color[] pieceColors;

    public TetrisWidget() {
        newGame(2000);
    }

    public TetrisGame getGame() {
        return game;
    }

    public void newGame(int timeToFall) {
        if (game != null) {
            game.terminateGame();
        }
        game = new TetrisGame(this, timeToFall);
        GameSettings gs = GameSettings.getInstance();
        if (gs.RANDOM_COLORS) {
            generateRandomColors();
        } else {
            pieceColors = new Color[]{gs.O, gs.L, gs.J, gs.T, gs.Z, gs.S, gs.I};
        }
        this.repaint();
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
        int yErrorFactor = TetrisPlan.HEIGHT - 20; // Ensures that there are always 20 visible lines by hiding the rest
        GameSettings settings = GameSettings.getInstance();

        int[][] board = game.getPlan().getBoard();

        int s = getScaling(board.length, board[0].length - yErrorFactor);
        int xOffset = getXOffset(board.length, board[0].length - yErrorFactor);

        // Draw background
        g.setColor(BG);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Draw board
        for (int x = 0; x < board.length; x++) {
            for (int y = yErrorFactor; y < board[0].length; y++) {
                g.setColor(GRID_COLOR);
                g.drawRect(xOffset + x * s, (y - yErrorFactor) * s, s, s);
                if (board[x][y] == -1) {
                    continue;
                }
                g.setColor(pieceColors[board[x][y]]);
                g.fillRect(xOffset + x * s, (y - yErrorFactor) * s, s, s);
            }
        }

        // Draw current piece
        int[][] piece = PlanVisual.getCurrentPiece(game.getPlan());
        int xStart = xOffset;
        drawBoard(piece, xStart, 0, g, s, null);

        // Draw piece shadow
        piece = PlanVisual.getPieceShadow(game.getPlan());
        int index = game.getPlan().getPiece().getIndex();
        Color c = pieceColors[index];
        Color opaque = new Color(c.getRed(), c.getGreen(), c.getBlue(), settings.SHADOW_PIECE_ALPHA_VALUE);
        drawBoard(piece, xStart, 0, g, s, opaque);

        // Draw hold piece
        piece = PlanVisual.getHoldPiece(game.getPlan());
        xStart = xOffset - (piece.length + 1) * s; // + 1 for a 1 square wide space between board and hold piece
        drawPiece(piece, xStart, 0, g, s);

        // Draw line count
        String lineCount = "Lines cleared: " + game.getPlan().getLineClearCount();
        int size = this.getHeight() / 20;
        int yStart = this.getHeight() - size;
        g.setFont(new Font("LineCountFont", Font.PLAIN, size));
        g.setColor(FONT_COLOR);
        g.drawString(lineCount, 0, yStart);

        // Draw next piece
        List<int[][]> pieces = PlanVisual.getNextPiece(game.getPlan());
        xStart = xOffset + (board.length + 1) * s; // + 1 for a 1 square wide space between board and hold piece
        yStart = 0;
        for (int[][] p : pieces) {
            yStart = drawPiece(p, xStart, yStart, g, s).y;
            yStart += s / 3;
        }
    }

    private void drawBoard(int[][] board, int xStart, int yStart, Graphics g, int s, Color pieceColor) {
        int yErrorFactor = TetrisPlan.HEIGHT - 20; // Ensures that there are always 20 visible lines by hiding the rest
        for (int x = 0; x < board.length; x++) {
            for (int y = yErrorFactor; y < board[0].length; y++) {
                g.setColor(GRID_COLOR);
                g.drawRect(xStart + x * s, yStart + (y - yErrorFactor) * s, s, s);
                if (board[x][y] == -1) {
                    continue;
                }
                if (pieceColor == null) {
                    pieceColor = pieceColors[board[x][y]];
                }
                g.setColor(pieceColor);
                g.fillRect(xStart + x * s, yStart + (y - yErrorFactor) * s, s, s);
            }
        }
    }

    private Point drawPiece(int[][] piece, int xStart, int yStart, Graphics g, int s) {
        for (int x = 0; x < piece.length; x++) {
            for (int y = 0; y < piece[0].length; y++) {
                g.setColor(GRID_COLOR);
                g.drawRect(xStart + x * s, yStart + y * s, s, s);
                if (piece[x][y] == -1) {
                    continue;
                }
                g.setColor(pieceColors[piece[x][y]]);
                g.fillRect(xStart + x * s, yStart + y * s, s, s);
            }
        }
        int xMax = xStart + (piece.length) * s;
        int yMax = yStart + (piece[0].length) * s;
        return new Point(xMax, yMax);
    }

}
