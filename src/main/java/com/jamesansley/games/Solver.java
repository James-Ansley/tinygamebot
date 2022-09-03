package com.jamesansley.games;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public class Solver {
    private final Node root;
    private final Player rootPlayer;

    public Solver(Game game, int depth) {
        this.rootPlayer = game.getToMovePlayer();
        this.root = new Node(game, null, true);
        construct(root, depth);
    }

    public int getBestMove() {
        Node bestChild = root.children.stream().max(comparing(Node::score)).orElseThrow();
        return bestChild.move;
    }

    private void construct(Node root, int depth) {
        List<Integer> moves = root.game.validMoves();
        for (int move : moves) {
            Node child = new Node(root.game.move(move), move, !root.isMax);
            root.children.add(child);
            if (depth >= 0 && !child.game.isFinished()) {
                construct(child, depth - 1);
            }
        }
    }

    private class Node {
        private final Game game;
        private final Integer move;
        private final List<Node> children;
        private final boolean isMax;

        public Node(Game game, Integer move, boolean isMax) {
            this.game = game;
            this.move = move;
            this.isMax = isMax;
            this.children = new ArrayList<>();
        }

        public double score() {
            double currentScore = game.heuristic(Solver.this.rootPlayer);
            if (children.isEmpty()) {
                return currentScore;
            }
            Stream<Double> childrenScores = children.stream().map(Node::score);
            if (isMax) {
                return childrenScores.max(Double::compareTo).orElseThrow();
            } else {
                return childrenScores.min(Double::compareTo).orElseThrow();
            }
        }
    }
}
