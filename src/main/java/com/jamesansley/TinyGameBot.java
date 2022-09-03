package com.jamesansley;

import com.jamesansley.games.Game;
import com.jamesansley.games.GameFactory;
import com.jamesansley.games.Solver;
import com.jamesansley.games.exceptions.UnparseableGameString;
import com.jamesansley.games.join4.Join4;
import com.jamesansley.twitter.TwitterClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.model.Tweet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jamesansley.utils.Collections.getRandom;


public class TinyGameBot {
    public static void main(String[] args) throws ApiException, UnparseableGameString {
        TwitterClient client = new TwitterClient();
        Tweet lastTweet = client.getLastPost();
        Game game = GameFactory.fromString(lastTweet.getText());

        Set<Integer> validMoves = new HashSet<>(game.validMoves());

        List<Integer> replies = client.getReplies(lastTweet.getId());
        replies = replies.stream().filter(validMoves::contains).toList();
        System.out.println(replies);

        int move = getRandom(replies);
        game = game.move(move);

        if (!game.isFinished()) {
            Solver solver = new Solver(game, 4);
            int botMove = solver.getBestMove();
            game = game.move(botMove);
        }

        System.out.println(game);
        client.post(game.toString());

        if (game.isFinished()) {
            Game newGame = new Join4(game.getGameNum() + 1);
            client.post(newGame.toString());
        }
    }
}
