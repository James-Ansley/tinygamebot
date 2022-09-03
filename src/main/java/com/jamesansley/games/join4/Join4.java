package com.jamesansley.games.join4;

import com.jamesansley.games.Game;
import com.jamesansley.games.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.jamesansley.utils.Collections.*;
import static com.jamesansley.utils.IO.numberBar;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.joining;

public class Join4 extends Game {
    public static final int WIDTH = 7;
    public static final int HEIGHT = 6;
    public static final String DISPLAY_NAME = "Join4";
    public static final Player BOT_PLAYER = Piece.RED;

    private final List<List<Player>> grid;
    private final Player lastMovedPlayer;
    private final List<Integer> heights;
    private final List<List<Player>> contiguousRuns;

    public Join4(int gameNumber) {
        this(gameNumber, 1, nCopies(HEIGHT, nCopies(WIDTH, Piece.EMPTY)), nCopies(WIDTH, 0), null);
    }

    protected Join4(int gameNum, int turnNum, List<List<Player>> grid, List<Integer> heights, Player lastMovedPlayer) {
        super(gameNum, turnNum);
        this.grid = grid;
        this.heights = heights;
        this.lastMovedPlayer = lastMovedPlayer;
        this.contiguousRuns = getContiguousRuns();
    }

    @Override
    public String getGameName() {
        return DISPLAY_NAME;
    }

    @Override
    public Join4 makeMove(int col) {
        col--;
        int row = heights.get(col);

        List<List<Player>> newGrid = new ArrayList<>(grid.stream().map(ArrayList::new).toList());
        List<Integer> newHeights = new ArrayList<>(heights);

        newGrid.get(row).set(col, getToMovePlayer());
        newHeights.set(col, row + 1);
        return new Join4(
                getGameNum(),
                getTurnNum() + 1,
                newGrid,
                newHeights,
                getToMovePlayer()
        );
    }

    @Override
    public List<Integer> validMoves() {
        return IntStream.range(1, WIDTH + 1)
                .filter(i -> heights.get(i - 1) < HEIGHT)
                .boxed()
                .toList();
    }

    @Override
    public boolean isFinished() {
        return hasWon(getBotPlayer()) || hasWon(getTwitterPlayer());
    }

    @Override
    public boolean hasWon(Player player) {
        return contiguousRuns.stream().anyMatch(row -> count(row, player) == 4);
    }

    @Override
    public double heuristic(Player player) {
        if (hasWon(player)) {
            return Double.POSITIVE_INFINITY;
        }
        if (hasWon(player.opponent())) {
            return Double.NEGATIVE_INFINITY;
        }
        double score = 0.0;
        for (List<Player> run : contiguousRuns) {
            int thisRunOf = count(run, player);
            int theirRunOf = count(run, player.opponent());
            if (theirRunOf == 0) {
                score += thisRunOf << 1;
            }
            if (thisRunOf == 0) {
                score -= theirRunOf << 1;
            }
        }
        return score;
    }

    @Override
    public Optional<Player> getLastMovedPlayer() {
        return Optional.ofNullable(lastMovedPlayer);
    }

    @Override
    public Player getBotPlayer() {
        return BOT_PLAYER;
    }

    @Override
    public Player getTwitterPlayer() {
        return BOT_PLAYER.opponent();
    }

    @Override
    public String toString() {
        String gridString = reversed(grid)
                .map(row -> row.stream()
                        .map(String::valueOf)
                        .collect(joining("")))
                .collect(joining("\n"));
        String prompt;
        if (isFinished()) {
            prompt = hasWon(getBotPlayer()) ? "I Win!" : "You Win!";
        } else {
            String playerPronoun = getToMovePlayer() == getTwitterPlayer() ? "Your" : "My";
            prompt = "%s Move, %s".formatted(playerPronoun, getToMovePlayer());
        }
        return String.join("\n", getGameInfo(), gridString, numberBar(1, WIDTH + 1), prompt);
    }

    private List<List<Player>> getContiguousRuns() {
        List<List<Player>> runs = new ArrayList<>();
        runs.addAll(grid.stream().flatMap(row -> windowed(row, 4)).toList());
        runs.addAll(transpose(grid).stream().flatMap(row -> windowed(row, 4)).toList());
        runs.addAll(diagonals(grid));
        return runs;
    }
}
