package com.jamesansley.games;

import com.jamesansley.games.exceptions.UnparseableGameString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Parser {
    private static final Pattern gameNamePattern = Pattern.compile("^(.+), Game \\d+-\\d+");
    private static final Pattern gameNumberPattern = Pattern.compile("^.+, Game (\\d+)-\\d+");
    private static final Pattern gameRoundPattern = Pattern.compile("^.+, Game \\d+-(\\d+)");

    private final String data;

    public Parser(String data) {
        this.data = data;
    }

    public static String getGameName(String data) throws UnparseableGameString {
        return new Parser(data) {
            public Game parse() {
                return null;
            }
        }.getGameName();
    }

    public final String getData() {
        return data;
    }

    public String getGameName() throws UnparseableGameString {
        return getMatchGroup(gameNamePattern);
    }

    public Integer getGameNumber() throws UnparseableGameString {
        String gameNumString = getMatchGroup(gameNumberPattern);
        return Integer.parseInt(gameNumString);
    }

    public Integer getRoundNumber() throws UnparseableGameString {
        String turnNumString = getMatchGroup(gameRoundPattern);
        return Integer.parseInt(turnNumString);
    }

    public abstract Game parse() throws UnparseableGameString;

    protected String getMatchGroup(Pattern pattern) throws UnparseableGameString {
        Matcher matcher = pattern.matcher(getData());
        if (!matcher.find()) {
            throw new UnparseableGameString(getData());
        }
        return matcher.group(1);
    }
}
