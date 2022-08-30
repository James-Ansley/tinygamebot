package com.jamesansley.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Collections {
    public static <T> Stream<T> reversed(List<T> data) {
        int size = data.size();
        return IntStream.range(0, size).map(i -> size - i - 1).mapToObj(data::get);
    }

    public static <T> Stream<T> reversed(T[] data) {
        int size = data.length;
        return IntStream.range(0, size).map(i -> size - i - 1).mapToObj(i -> data[i]);
    }

    public static <T> List<T> setValue(List<T> values, int col, T value) {
        values = new ArrayList<>(values);
        values.set(col, value);
        return values.stream().toList();
    }

    public static <T> List<List<T>> transpose(List<List<T>> data) {
        return IntStream.range(0, data.get(0).size())
                .mapToObj(i -> data.stream().map(l -> l.get(i)).toList())
                .toList();
    }

    public static <T> int count(List<T> data, T target) {
        return Math.toIntExact(data.stream().filter(e -> e.equals(target)).count());
    }

    public static <T> List<List<T>> setValue(List<List<T>> board, int i, int j, T value) {
        board = board.stream().map(ArrayList::new).collect(Collectors.toList());
        board.get(i).set(j, value);
        return board.stream().map(row -> row.stream().toList()).toList();
    }
}
