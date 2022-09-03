package com.jamesansley.games.exceptions;

public class UnparseableGameString extends Exception {
    public UnparseableGameString(String data) {
        super("Game string cannot be parsed to game:\n\"\"\"%s\"\"\"".formatted(data));
    }
}
