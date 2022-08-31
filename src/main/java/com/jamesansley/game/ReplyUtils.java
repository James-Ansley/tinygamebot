package com.jamesansley.game;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReplyUtils {
    public static List<Integer> cleanReplies(List<String> rawReplies, List<Integer> validMoves) {
        Set<Integer> moves = new HashSet<>(validMoves);
        return rawReplies
                .stream()
                .filter(s -> s.matches("\\d"))
                .map(Integer::valueOf)
                .filter(moves::contains)
                .toList();
    }
}
