package com.jamesansley.games;

import com.jamesansley.games.exceptions.UnparseableGameString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SolverTest {
    @Test
    public void testTakesFirstWin() throws UnparseableGameString {
        // 2 is also a possible winning move but 3 will win in fewer moves
        String gameString = """
                Join4, Game 1-10
                ⚪⚪⚪⚪⚪⚪⚪
                ⚪⚪🔴🔴⚪⚪⚪
                ⚪🔴🔴🔴⚪⚪⚪
                ⚪🟡🔴🟡⚪⚪⚪
                ⚪🟡🟡🔴🟡🟡⚪
                ⚪🟡🔴🟡🟡🟡🔴
                1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣
                My Move, 🔴""";
        Game game = GameFactory.fromString(gameString);
        Solver solver = new Solver(game, 4);
        assertEquals(3, solver.getBestMove());
    }
}
