package com.jamesansley.games;

import java.util.ArrayList;
import java.util.List;

import static com.jamesansley.utils.Collections.getRandom;
import static java.util.Comparator.comparing;

public class Solver {
    private final Node root;
    private final Player rootPlayer;

    public Solver(Game game, int depth) {
        this.rootPlayer = game.getToMovePlayer();
        this.root = new Node(game, null, true, 0);
        construct(root, depth);
    }

    public int getBestMove() {
        return getRandom(root.bestMoves());
    }

    private void construct(Node root, int depth) {
        List<Integer> moves = root.game.validMoves();
        for (int move : moves) {
            Node child = new Node(root.game.move(move), move, !root.isMax, root.depth + 1);
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
        private final int depth;

        private ScoreToDepth scoreToDepth = null;

        public Node(Game game, Integer move, boolean isMax, int depth) {
            this.game = game;
            this.move = move;
            this.isMax = isMax;
            this.depth = depth;
            this.children = new ArrayList<>();
        }

        public List<Integer> bestMoves() {
            Node.ScoreToDepth bestScore = root.scoreWithDepth();
            return root.children
                    .stream()
                    .filter(n -> n.scoreWithDepth().score.equals(bestScore.score))
                    .filter(n -> n.scoreWithDepth().depth == bestScore.depth - 1)
                    .map(n -> n.move)
                    .toList();
        }

        private ScoreToDepth scoreWithDepth() {
            if (scoreToDepth != null) {
                return scoreToDepth;
            }

            if (children.isEmpty()) {
                double currentScore = game.heuristic(Solver.this.rootPlayer);
                scoreToDepth = new ScoreToDepth(currentScore, 0);
            } else {
                List<ScoreToDepth> childrenScores = children.stream().map(Node::scoreWithDepth).toList();

                double score;
                if (isMax) {
                    score = childrenScores.stream().max(comparing(ScoreToDepth::score)).orElseThrow().score();
                } else {
                    score = childrenScores.stream().min(comparing(ScoreToDepth::score)).orElseThrow().score();
                }

                ScoreToDepth shallowestScore = childrenScores
                        .stream()
                        .filter(scoreToDepth -> scoreToDepth.score() == score)
                        .min(comparing(ScoreToDepth::depth))
                        .orElseThrow();
                scoreToDepth = new ScoreToDepth(score, shallowestScore.depth() + 1);
            }
            return scoreToDepth;
        }

        private record ScoreToDepth(Double score, Integer depth) {
        }
    }
}
