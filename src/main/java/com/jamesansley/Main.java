package com.jamesansley;

import com.jamesansley.game.Join4;
import com.jamesansley.game.Solver;
import com.jamesansley.twitter.TwitterClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.model.Tweet;

import java.util.List;
import java.util.Scanner;

import static com.jamesansley.game.ReplyUtils.cleanReplies;
import static com.jamesansley.utils.Collections.getRandom;


public class Main {
    public static void main(String[] args) throws ApiException {
        continueGame();
    }

    public static void newGame(int num) throws ApiException {
        TwitterClient client = new TwitterClient();
        Join4 game = new Join4(num);

        client.post(game.toString());
    }

    public static void continueGame() throws ApiException {
        TwitterClient client = new TwitterClient();
        Tweet lastTweet = client.getLastPost();
        Join4 game = Join4.fromString(lastTweet.getText());

        List<String> rawReplies = client.getReplies(lastTweet.getId());
        List<Integer> replies = cleanReplies(rawReplies, game.validMoves());
        System.out.println(replies);

        int move = getRandom(replies);
        game = game.move(move);

        Solver solver = new Solver(game);
        int botMove = solver.getBestMove();
        game = game.move(botMove);

        System.out.println(game);

        client.post(game.toString());
    }

    public static void playLocal() {
        Join4 game = new Join4(1);
        Scanner s = new Scanner(System.in);

        System.out.println(game);
        while (!game.isWin()) {
            System.out.print("Your Move > ");
            int move = s.nextInt();
            game = game.move(move);
            System.out.println(game);
            System.out.printf("%s score: %f%n", game.lastMovedPlayer, game.heuristic());
            Solver solver = new Solver(game);
            int botMove = solver.getBestMove();
            System.out.printf("Bot Move > %d%n", botMove);
            game = game.move(botMove);
            System.out.println(game);
            System.out.printf("%s score: %f%n", game.lastMovedPlayer, game.heuristic());
        }
    }

    public static void botVsBot() {
        Join4 game = new Join4(1);

        System.out.println(game);
        while (!game.isWin()) {
            Solver solver1 = new Solver(game);
            game = game.move(solver1.getBestMove());
            System.out.println(game);
            Solver solver2 = new Solver(game);
            game = game.move(solver2.getBestMove());
            System.out.println(game);
        }
    }
}
