package com.jamesansley;

import com.jamesansley.game.Join4;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AppTest {
    @Test
    public void emptyBoardString() {
        Join4 game = new Join4(1);
        String gameString = game.toString();
        String expected = """
                Join4 - Game 1-1
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪⚪⚪⚪⚪⚪
                1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣
                Your Move, 🟡""";
        assertEquals(expected, gameString);
    }

    @Test
    public void afterMove() {
        Join4 game = new Join4(1);
        Join4 nextMove = game.move(3);
        assertEquals(Join4.Piece.EMPTY, game.grid.get(0).get(2));

        assertEquals(Join4.Piece.YELLOW, nextMove.grid.get(0).get(2));
        assertEquals(2, nextMove.currentMoveNumber);
    }

    @Test
    public void afterMoveBoardString() {
        Join4 game = new Join4(1).move(3).move(3);
        String gameString = game.toString();
        String expected = """
                Join4 - Game 1-3
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪🔴⚪⚪⚪⚪
                ⚪⚪🟡⚪⚪⚪⚪
                1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣
                Your Move, 🟡""";
        assertEquals(expected, gameString);
    }
}
