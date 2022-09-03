package com.jamesansley.games.join4;

import com.jamesansley.games.Game;
import org.junit.Test;

import static org.junit.Assert.*;

public class Join4Test {

    public static final String EMPTY_BOARD_STRING = """
            Join4, Game 1-1
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣
            Your Move, 🟡""";

    public static final String SINGLE_MOVE_BOARD_STRING = """
            Join4, Game 1-1
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪🟡⚪⚪⚪
            1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣
            My Move, 🔴""";
    public static final String MANY_MOVE_BOARD_STRING = """
            Join4, Game 1-5
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪🟡🔴⚪⚪⚪
            🔴🔴🟡🟡🟡🔴🟡
            1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣
            My Move, 🔴""";

    public static final String TWITTER_WIN_BOARD_STRING = """
            Join4, Game 1-4
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪🟡⚪⚪⚪
            ⚪⚪⚪🟡⚪⚪⚪
            ⚪⚪⚪🟡⚪⚪⚪
            🔴🔴🔴🟡⚪⚪⚪
            1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣
            You Win!""";

    public static final String BOT_WIN_BOARD_STRING = """
            Join4, Game 1-5
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪⚪⚪⚪⚪
            ⚪⚪⚪🔴⚪⚪⚪
            ⚪⚪⚪🔴⚪⚪⚪
            ⚪⚪⚪🔴⚪⚪⚪
            🟡🟡🟡🔴🟡⚪⚪
            1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣
            I Win!""";

    private static Game runSequence(int[] moveSequence, Game game) {
        for (int move : moveSequence) {
            game = game.move(move);
        }
        return game;
    }

    @Test
    public void testNewGame() {
        Game game = new Join4(1);
        assertEquals(1, game.getGameNum());
        assertEquals(1, game.getCurrentRoundNum());
        assertEquals(1, game.getTurnNum());
        assertEquals(Piece.RED, game.getBotPlayer());  // ToDo – Reconsider this behaviour
        assertEquals(Piece.YELLOW, game.getTwitterPlayer());  // might allow custom start players
        assertTrue(game.getLastMovedPlayer().isEmpty());
        assertEquals(EMPTY_BOARD_STRING, game.toString()
        );
    }

    @Test
    public void testMove() {
        Game blankGame = new Join4(1);
        Game nextTurn = blankGame.move(4);

        assertEquals(EMPTY_BOARD_STRING, blankGame.toString());
        assertEquals(SINGLE_MOVE_BOARD_STRING, nextTurn.toString());

        assertEquals(Piece.YELLOW, nextTurn.getLastMovedPlayer().orElseThrow());
        assertEquals(Piece.RED, nextTurn.getToMovePlayer());
        assertEquals(1, nextTurn.getGameNum());
        assertEquals(1, nextTurn.getCurrentRoundNum());
        assertEquals(2, nextTurn.getTurnNum());
    }

    @Test
    public void testManyMoves() {
        int[] moveSequence = {4, 4, 3, 2, 5, 6, 3, 1, 7};
        Game game = runSequence(moveSequence, new Join4(1));
        assertEquals(MANY_MOVE_BOARD_STRING, game.toString());
        assertEquals(moveSequence.length + 1, game.getTurnNum());
        assertEquals(5, game.getCurrentRoundNum());
        assertEquals(Piece.YELLOW, game.getLastMovedPlayer().orElseThrow());
        assertEquals(Piece.RED, game.getToMovePlayer());
    }

    @Test
    public void testFinishGame() {
        int[] moveSequence = {4, 3, 4, 2, 4, 1, 4};
        Game game = runSequence(moveSequence, new Join4(1));
        assertEquals(TWITTER_WIN_BOARD_STRING, game.toString());
        assertTrue(game.isFinished());
        assertTrue(game.hasWon(game.getTwitterPlayer()));
        assertFalse(game.hasWon(game.getBotPlayer()));

        moveSequence = new int[]{1, 4, 2, 4, 3, 4, 5, 4};
        game = runSequence(moveSequence, new Join4(1));
        assertEquals(BOT_WIN_BOARD_STRING, game.toString());
        assertTrue(game.isFinished());
        assertTrue(game.hasWon(game.getBotPlayer()));
        assertFalse(game.hasWon(game.getTwitterPlayer()));
    }

    @Test
    public void testThrowsOnFullColumnMove() {
        Game game = new Join4(1);
        for (int i = 0; i < Join4.HEIGHT; i++) {
            game = game.move(4);
        }
        Game finalGame = game;
        assertThrows(IllegalArgumentException.class, () -> finalGame.move(4));
    }

    @Test
    public void testThrowsOnOutOfBoundsMove() {
        Game game = new Join4(1);
        assertThrows(IllegalArgumentException.class, () -> game.move(0));
        assertThrows(IllegalArgumentException.class, () -> game.move(Join4.WIDTH + 1));
    }

    @Test
    public void testThrowsOnFinishedGameMove() {
        int[] moveSequence = {4, 4, 3, 3, 2, 2, 5};
        Game game = runSequence(moveSequence, new Join4(1));
        assertThrows(IllegalStateException.class, () -> game.move(5));
    }
}
