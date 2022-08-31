package com.jamesansley.game;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class Solver {
    public static int maxDepth = 4;

    private final Node root;

    public Solver(Join4 game) {
        root = new Node(game, null, true);
        construct(root, maxDepth);
    }

    public int getBestMove() {
        Node bestChild = root.children.stream().max(comparing(Node::score)).orElseThrow();
        return bestChild.move;
    }

    public void construct(Node root, int depth) {
        List<Integer> moves = root.game.validMoves();
        for (int move : moves) {
            Node child = new Node(root.game.move(move), move, !root.isMax);
            root.children.add(child);
            if (depth >= 0 && !child.game.isWin()) {
                construct(child, depth - 1);
            }
        }
    }

    static class Node {
        Join4 game;
        Integer move;
        List<Node> children;
        boolean isMax;

        public Node(Join4 game, Integer move, boolean isMax) {
            this.game = game;
            this.move = move;
            this.isMax = isMax;
            this.children = new ArrayList<>();
        }

        public double score() {
            double currentScore = game.heuristic();
            if (children.isEmpty()) {
                // if isMax, parent is trying to find min – invert root nodes to flip heuristic
                return isMax ? -currentScore : currentScore;
            }
            List<Double> childrenScores = children.stream().map(Node::score).toList();
            if (isMax) {
                return childrenScores.stream().max(Double::compareTo).orElseThrow();
            } else {
                return childrenScores.stream().min(Double::compareTo).orElseThrow();
            }
        }
    }
}
