package tetris.controller;

import java.lang.Thread.State;
import java.util.concurrent.locks.LockSupport;
import tetris.model.TetrisPlan;
import tetris.view.Popups;
import tetris.view.TetrisWidget;

/**
 * Controller used to handle game logic, game loop and user inputs like soft
 * dropping, hard dropping, rotating, moving left and right.
 *
 * @author davidwolf
 */
public class TetrisGame {

    private TetrisPlan plan;
    private TetrisWidget callback;
    private Thread gameLoopThread;
    private Thread softDropThread;
    private Thread leftRightThread;

    public TetrisGame(TetrisWidget callback, int timeToFall) {
        plan = new TetrisPlan();
        this.callback = callback;

        plan.newNextPiece();
        plan.newPiece();
        plan.newNextPiece();
        initiateThreads(timeToFall);
    }

    public TetrisPlan getPlan() {
        return plan;
    }

    public void hardDrop() {
        if (!plan.isPlaying()) {
            return;
        }
        while (plan.move(0, 1)) {
        }
        plan.placePiece();
        plan.newPiece();
        plan.newNextPiece();
        callback.repaint();
    }

    public void softDropStart() {
        if (softDropThread.getState() == State.WAITING) {
            gameLoopThread.interrupt();
            System.out.println("unparked soft from start");
            LockSupport.unpark(softDropThread);
        }
    }

    public void softDropEnd() {
        if (softDropThread.getState() != State.WAITING) {
            softDropThread.interrupt();
            System.out.println("unparked game from end");
            LockSupport.unpark(gameLoopThread);
        }
    }

    public void hold() {
        if (plan.hold()) {
            this.callback.repaint();
        }
    }

    public void rotate(int rotateTimes) {
        if (!plan.isPlaying()) {
            return;
        }
        for (int i = 0; i < rotateTimes; i++) {
            plan.getPiece().rotate();
        }
        settlePieceInPlace();
        if (!plan.move(0, 0)) {
            for (int i = 0; i < 4 - rotateTimes; i++) {
                plan.getPiece().rotate();
            }
            return;
        }
        this.callback.repaint();
    }

    /**
     * Method used to move around a piece after rotating to find a valid place.
     * Allows the piece to rotate when close to the ceiling or to the walls.
     */
    private void settlePieceInPlace() {
        if (plan.move(0, 0)
                || plan.move(0, 1)
                || plan.move(1, 0) || plan.move(-1, 0)
                || plan.move(2, 0) || plan.move(-2, 0)) {
        }
    }

    public void moveDirection(int direction) {
        if (!plan.isPlaying()) {
            return;
        }
        plan.move(direction, 0);
        callback.repaint();
    }

    private void initiateThreads(int timeToFall) {
        gameLoopThread = Thread.ofVirtual().start(() -> {
            while (plan.isPlaying()) {
                try {
                    Thread.sleep(timeToFall);
                    System.out.println("game running");
                    if (!plan.move(0, 1)) {
                        plan.placePiece();
                        plan.newPiece();
                        plan.newNextPiece();
                    }
                    callback.repaint();
                } catch (InterruptedException ex) {
                    System.out.println("parked game");
                    LockSupport.park();
                    System.out.println("unparked game");
                }
            }
            Popups.gameSpeed();
        });

        softDropThread = Thread.ofVirtual().start(() -> {
            LockSupport.park();
            while (plan.isPlaying()) {
                try {
                    Thread.sleep(1000);
                    System.out.println("soft running");
                    if (!plan.move(0, 1)) {
                        System.out.println("piece fell and unparked game");
                        LockSupport.unpark(gameLoopThread);
                        LockSupport.park();
                        System.out.println("unparked soft");
                    }
                    callback.repaint();
                } catch (InterruptedException ex) {
                    System.out.println("parked soft");
                    LockSupport.park();
                    System.out.println("unparked soft");
                }
            }
        });
    }
}
