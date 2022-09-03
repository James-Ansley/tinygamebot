package com.jamesansley.games.join4;

import com.jamesansley.games.Player;

public enum Piece implements Player {
    RED("\uD83D\uDD34"), YELLOW("\uD83D\uDFE1"), EMPTY("\u26AA");

    private final String symbol;

    Piece(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public Player opponent() {
        if (this == Piece.EMPTY) {
            throw new IllegalStateException("Empty Piece has no opponent.");
        }
        return this == Piece.RED ? Piece.YELLOW : Piece.RED;
    }

    public static Player fromString(String symbol) {
        return switch (symbol) {
            case "\uD83D\uDD34" -> RED;
            case "\uD83D\uDFE1" -> YELLOW;
            case "\u26AA" -> EMPTY;
            default -> throw new IllegalArgumentException("Cannot parse symbol %s".formatted(symbol));
        };
    }
}
