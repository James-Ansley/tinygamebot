package com.jamesansley.utils;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public class IO {
    public static String keyCap(String symbol) {
        String keyCapSymbol = "\uFE0F\u20E3";
        return "%s%s".formatted(symbol, keyCapSymbol);
    }

    public static String keyCap(int value) {
        return keyCap(String.valueOf(value));
    }

    public static String numberBar(int startInclusive, int endExclusive) {
        return IntStream
                .range(startInclusive, endExclusive)
                .mapToObj(IO::keyCap)
                .collect(joining(""));
    }
}
