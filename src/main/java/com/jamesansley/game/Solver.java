package com.jamesansley.game;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class Solver {
    public static int maxDepth = 4;

    private final Node root;
    private final Join4.Piece player;

    public Solver(Join4 game, Join4.Piece player) {
        this.root = new Node(game, null, player, true);
        this.player = player;
        construct(root, maxDepth);
    }

    public int getBestMove() {
        Node bestChild = root.children.stream().max(comparing(Node::score)).orElseThrow();
        return bestChild.move;
    }

    public void construct(Node root, int depth) {
        List<Integer> moves = root.game.validMoves();
        for (int move : moves) {
            Node child = new Node(root.game.move(move), move, player, !root.isMax);
            root.children.add(child);
            if (depth >= 0 && !child.game.isWin()) {
                construct(child, depth - 1);
            }
        }
    }

    static class Node {
        Join4 game;
        Integer move;
        Join4.Piece player;
        List<Node> children;
        boolean isMax;

        public Node(Join4 game, Integer move, Join4.Piece player, boolean isMax) {
            this.game = game;
            this.move = move;
            this.isMax = isMax;
            this.player = player;
            this.children = new ArrayList<>();
        }

        public double score() {
            double currentScore = game.heuristic(player);
            if (children.isEmpty()) {
                return currentScore;
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
