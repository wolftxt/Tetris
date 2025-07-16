package tetris.model;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class TetrisPlanTest {

    public TetrisPlanTest() {
    }

    @Test
    public void testMoveDown() {
        TetrisPlan plan = new TetrisPlan();
        plan.newNextPieces();
        plan.newPiece();
        for (int i = 0; i < 18; i++) {
            Assert.assertEquals(plan.move(0, 1), true);
        }
        plan.move(0, 1);
        Assert.assertEquals(plan.move(0, 1), false);
    }

    @Test
    public void testPlacePiece() {
        TetrisPlan plan = new TetrisPlan();
        plan.newNextPieces();
        plan.newPiece();
        for (int i = 0; i < 19; i++) {
            plan.move(0, 1);
        }
        plan.placePiece();
        plan.newPiece();
        for (int i = 0; i < 18; i++) {
            plan.move(0, 1);
        }
        Assert.assertEquals(plan.move(0, 1), false);
    }
}
