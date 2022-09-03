package com.jamesansley.games;

import java.util.List;
import java.util.Optional;

public abstract class Game {
    private final int gameNum;
    private final int turnNum;

    public Game(int gameNum, int turnNum) {
        this.gameNum = gameNum;
        this.turnNum = turnNum;
    }

    /**
     * @return The current game number
     */
    public final int getGameNum() {
        return gameNum;
    }

    /**
     * @return The current turn number (a turn is added whenever a player makes a move)
     */
    public final int getTurnNum() {
        return turnNum;
    }

    /**
     * @return The current round number (a round consists of both players moving)
     */
    public final int getCurrentRoundNum() {
        return (turnNum + 1) / 2;
    }

    /**
     * @param move The index of the next move
     * @return A new Game instance where the given move has been performed.
     * @throws IllegalStateException    if a move is performed on a game instance that is finished
     * @throws IllegalArgumentException if a move is given that is not valid
     */
    public final Game move(int move) {
        if (isFinished()) {
            throw new IllegalStateException("Cannot do move on finished Game");
        }
        if (!validMoves().contains(move)) {
            throw new IllegalArgumentException("Move, %d, is not valid, %s".formatted(move, validMoves()));
        }
        return makeMove(move);
    }

    /**
     * @return A string containing information about the game of the form: &lt;GameName>, &lt;GameNum>-&lt;RoundNum>
     */
    public final String getGameInfo() {
        return "%s, Game %d-%d".formatted(getGameName(), getGameNum(), getCurrentRoundNum());
    }

    /**
     * @return The display name of the game type
     */
    public abstract String getGameName();

    /**
     * Constructs the next game state.
     * It can be assumed that this method will only be called with valid moves on a Game that is not yet finished.
     *
     * @param move The index of the next move
     * @return A new Game instance where the given move has been performed.
     */
    protected abstract Game makeMove(int move);

    /**
     * @return A list of valid moves that can be played next
     */
    public abstract List<Integer> validMoves();

    /**
     * @return True if the game has been completed and false otherwise
     */
    public abstract boolean isFinished();

    /**
     * @param player The player to check the win-status of
     * @return True if the given player parameter has won the game
     */
    public abstract boolean hasWon(Player player);

    /**
     * @param player The player to be scored
     * @return The heuristic score of the performance of the given player
     */
    public abstract double heuristic(Player player);

    /**
     * @return The player who has just moved
     */
    public abstract Optional<Player> getLastMovedPlayer();

    /**
     * @return The player that will move next
     */
    public Player getToMovePlayer() {
        // First player defaults to Twitter Player
        if (getLastMovedPlayer().isEmpty()) {
            return getTwitterPlayer();
        }
        return getLastMovedPlayer().get().opponent();
    }

    /**
     * @return The player object that represents the bot
     */
    public abstract Player getBotPlayer();

    /**
     * @return The player object that represents the twitter players
     */
    public abstract Player getTwitterPlayer();

    /**
     * @return A String display representation of that game that should contain enough information to recreate the Game.
     */
    @Override
    public abstract String toString();
}
