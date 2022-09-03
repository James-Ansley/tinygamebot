package com.jamesansley.games.join4;

import com.jamesansley.games.Game;
import com.jamesansley.games.Parser;
import com.jamesansley.games.Player;
import com.jamesansley.games.exceptions.UnparseableGameString;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static com.jamesansley.utils.Collections.*;

public class Join4Parser extends Parser {
    private static final Pattern gridPattern = Pattern.compile("((?:[🟡⚪🔴]{7}\\n){6})");
    private static final Pattern toMovePlayerPiecePattern = Pattern.compile("(?:Your|My) Move, ([🟡🔴])$");
    private static final Pattern winnerPronounPattern = Pattern.compile("(You|I) Win!$");

    private List<List<Player>> grid;

    public Join4Parser(String data) {
        super(data);
    }

    @Override
    public Game parse() throws UnparseableGameString {
        return new Join4(
                getGameNumber(),
                getTurnNumber(),
                getGrid(),
                getHeights(),
                getLastMovedPlayer()
        );
    }

    private List<List<Player>> getGrid() throws UnparseableGameString {
        if (grid == null) {
            String gridData = getMatchGroup(gridPattern);
            grid = reversed(gridData.split("\n"))
                    .map(row -> row.codePoints()
                            .mapToObj(Character::toString)
                            .map(Piece::fromString)
                            .toList())
                    .toList();
        }
        return grid;
    }

    private int getTurnNumber() throws UnparseableGameString {
        List<Player> pieces = getGrid().stream().flatMap(Collection::stream).toList();
        return Join4.WIDTH * Join4.HEIGHT - count(pieces, Piece.EMPTY) + 1;
    }

    private List<Integer> getHeights() throws UnparseableGameString {
        return transpose(getGrid()).stream()
                .map(row -> Join4.HEIGHT - count(row, Piece.EMPTY))
                .toList();
    }

    private Player getLastMovedPlayer() throws UnparseableGameString {
        if (getTurnNumber() == 1) {
            return null;
        }
        try {
            String toMovePiece = getMatchGroup(toMovePlayerPiecePattern);
            return Piece.fromString(toMovePiece).opponent();
        } catch (UnparseableGameString e) {
            String winnerPronoun = getMatchGroup(winnerPronounPattern);
            return winnerPronoun.equals("I") ? Join4.BOT_PLAYER : Join4.BOT_PLAYER.opponent();
        }
    }
}
