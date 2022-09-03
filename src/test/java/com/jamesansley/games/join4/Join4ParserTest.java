package com.jamesansley.games.join4;

import com.jamesansley.games.Game;
import com.jamesansley.games.Parser;
import com.jamesansley.games.exceptions.UnparseableGameString;
import org.junit.Test;

import static org.junit.Assert.*;

public class Join4ParserTest {
    @Test
    public void testParseBlank() throws UnparseableGameString {
        Parser p = new Join4Parser(Join4Test.EMPTY_BOARD_STRING);
        Game game = p.parse();
        assertEquals(1, game.getGameNum());
        assertEquals(1, game.getCurrentRoundNum());
        assertEquals(1, game.getTurnNum());
        assertEquals(Piece.RED, game.getBotPlayer());
        assertEquals(Piece.YELLOW, game.getTwitterPlayer());
        assertTrue(game.getLastMovedPlayer().isEmpty());
        assertEquals(Join4Test.EMPTY_BOARD_STRING, game.toString());
    }

    @Test
    public void testParseSingleMove() throws UnparseableGameString {
        Parser p = new Join4Parser(Join4Test.SINGLE_MOVE_BOARD_STRING);
        Game game = p.parse();
        assertEquals(Join4Test.SINGLE_MOVE_BOARD_STRING, game.toString());
        assertEquals(Piece.YELLOW, game.getLastMovedPlayer().orElseThrow());
        assertEquals(Piece.RED, game.getToMovePlayer());
        assertEquals(1, game.getGameNum());
        assertEquals(1, game.getCurrentRoundNum());
        assertEquals(2, game.getTurnNum());
    }

    @Test
    public void testParseManyMoves() throws UnparseableGameString {
        Parser p = new Join4Parser(Join4Test.MANY_MOVE_BOARD_STRING);
        Game game = p.parse();
        assertEquals(Join4Test.MANY_MOVE_BOARD_STRING, game.toString());
        assertEquals(10, game.getTurnNum());
        assertEquals(5, game.getCurrentRoundNum());
        assertEquals(Piece.YELLOW, game.getLastMovedPlayer().orElseThrow());
        assertEquals(Piece.RED, game.getToMovePlayer());
    }

    @Test
    public void testParseFinishedGame() throws UnparseableGameString {
        Parser p = new Join4Parser(Join4Test.TWITTER_WIN_BOARD_STRING);
        Game game = p.parse();
        assertEquals(Join4Test.TWITTER_WIN_BOARD_STRING, game.toString());
        assertTrue(game.isFinished());
        assertTrue(game.hasWon(game.getTwitterPlayer()));
        assertFalse(game.hasWon(game.getBotPlayer()));

        p = new Join4Parser(Join4Test.BOT_WIN_BOARD_STRING);
        game = p.parse();
        assertEquals(Join4Test.BOT_WIN_BOARD_STRING, game.toString());
        assertTrue(game.isFinished());
        assertTrue(game.hasWon(game.getBotPlayer()));
        assertFalse(game.hasWon(game.getTwitterPlayer()));
    }
}
