package com.jamesansley.game;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.jamesansley.utils.Collections.*;
import static com.jamesansley.utils.IO.numberBar;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.joining;

public class Join4 {
    private static final int width = 7;
    private static final int height = 6;
    private static final Pattern boardPattern = Pattern.compile(
            "Join4, Game (?<gameNum>\\d+)-(?<moveNum>\\d+)\\n"
            + "(?<board>(?:(?:\uD83D\uDFE1|\u26AA|\uD83D\uDD34){7}\\n){6})"
            + ".*\\n"
            + "Your Move, (?<nextPlayer>\uD83D\uDFE1|\u26AA|\uD83D\uDD34)"
    );

    public final int currentGameNumber;
    public final int currentMoveNumber;
    public final List<List<Piece>> grid;
    public final Piece lastMovedPlayer;
    private final List<Integer> heights;
    private List<List<Piece>> contiguousRuns = null;

    public Join4(int gameNumber) {
        this(gameNumber, 1, nCopies(height, nCopies(width, Piece.EMPTY)), nCopies(width, 0), null);
    }

    private Join4(
            int currentGameNumber,
            int currentMoveNumber,
            List<List<Piece>> grid,
            List<Integer> heights,
            Piece lastMovedPlayer
    ) {
        this.currentGameNumber = currentGameNumber;
        this.currentMoveNumber = currentMoveNumber;
        this.grid = grid;
        this.heights = heights;
        this.lastMovedPlayer = lastMovedPlayer;
    }

    /**
     * Makes a move by playing a piece in the given 1-based column.
     * This method does not mutate this.
     *
     * @param col the 1-based assumed valid column to play a piece
     * @return a new Join4 instance with the updated state
     */
    public Join4 move(int col) {
        col--;
        int row = heights.get(col);
        return new Join4(
                currentGameNumber,
                currentMoveNumber + 1,
                setValue(grid, row, col, Piece.flip(lastMovedPlayer)),
                setValue(heights, col, row + 1),
                Piece.flip(lastMovedPlayer)
        );
    }

    public List<Integer> validMoves() {
        return IntStream.range(1, width + 1)
                .filter(i -> heights.get(i - 1) < height)
                .boxed()
                .toList();
    }

    public boolean isWin() {
        return isWin(lastMovedPlayer);
    }

    public boolean isWin(Piece player) {
        return contiguousRuns().stream().anyMatch(row -> count(row, player) == 4);
    }

    public Double heuristic(Piece player) {
        if (isWin(player)) {
            return 2048.0;  // a big number
        }
        if (isWin(Piece.flip(player))) {
            return -2048.0;  // a big number
        }
        double score = 0.0;
        for (List<Piece> run : contiguousRuns()) {
            int thisRunOf = count(run, player);
            int theirRunOf = count(run, Piece.flip(player));
            if (theirRunOf == 0) {
                score += thisRunOf << 1;
            }
            if (thisRunOf == 0) {
                score -= theirRunOf << 1;
            }
        }
        return score;
    }

    public Double heuristic() {
        return heuristic(lastMovedPlayer);
    }

    @Override
    public String toString() {
        String gameInfo = "Join4, Game %d-%d".formatted(currentGameNumber, currentMoveNumber);
        String gridString = reversed(grid)
                .map(row -> row.stream().map(String::valueOf).collect(joining("")))
                .collect(joining("\n"));
        String prompt;
        if (isWin()) {
            prompt = "You Win, %s!".formatted(lastMovedPlayer);
        } else {
            prompt = "Your Move, %s".formatted(Piece.flip(lastMovedPlayer));
        }
        return String.join("\n", gameInfo, gridString, numberBar(1, width + 1), prompt);
    }

    private List<List<Piece>> contiguousRuns() {
        if (contiguousRuns == null) {
            contiguousRuns = new ArrayList<>();
            contiguousRuns.addAll(grid.stream().flatMap(row -> windowed(row, 4)).toList());
            contiguousRuns.addAll(transpose(grid).stream().flatMap(row -> windowed(row, 4)).toList());
            contiguousRuns.addAll(diagonals(grid));
        }
        return contiguousRuns;
    }

    public static Join4 fromString(String data) {
        Matcher matcher = boardPattern.matcher(data);
        if (!matcher.find()) {
            throw new RuntimeException("Unparsable Board");
        }
        return new Join4(
                Integer.parseInt(matcher.group("gameNum")),
                Integer.parseInt(matcher.group("moveNum")),
                parseGrid(matcher),
                parseHeights(parseGrid(matcher)),
                Piece.flip(Piece.fromString(matcher.group("nextPlayer")))
        );
    }

    private static List<Integer> parseHeights(List<List<Piece>> board) {
        return transpose(board).stream()
                .map(row -> height - count(row, Piece.EMPTY))
                .map(Math::toIntExact)
                .toList();
    }

    private static List<List<Piece>> parseGrid(Matcher matcher) {
        return reversed(matcher.group("board").split("\n"))
                .map(row -> row.codePoints()
                        .mapToObj(Character::toString)
                        .map(Piece::fromString)
                        .toList())
                .toList();
    }

    public enum Piece {
        RED("🔴"), YELLOW("\uD83D\uDFE1"), EMPTY("\u26AA");

        public final String symbol;

        Piece(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }

        public static Piece flip(Piece piece) {
            // First player defaults to Yellow
            if (piece == null) {
                return Piece.YELLOW;
            }
            return piece == Piece.RED ? Piece.YELLOW : Piece.RED;
        }

        public static Piece fromString(String symbol) {
            return switch (symbol) {
                case "\uD83D\uDD34" -> RED;
                case "\uD83D\uDFE1" -> YELLOW;
                case "\u26AA" -> EMPTY;
                default -> throw new RuntimeException("Cannot parse symbol %s".formatted(symbol));
            };
        }
    }
}