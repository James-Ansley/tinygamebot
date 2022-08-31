package com.jamesansley;

import com.jamesansley.game.Join4;
import com.jamesansley.twitter.TwitterClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.model.Tweet;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import static com.jamesansley.utils.Collections.getRandom;


public class Main {
    public static void main(String[] args) throws ApiException {
        TwitterClient client = new TwitterClient();
        Tweet lastTweet = client.getLastPost();

        Join4 game = Join4.fromString(lastTweet.getText());

        List<Tweet> replies = client.getHelperReplies(lastTweet.getId());
        List<String> replyIDs = replies.stream().map(Tweet::getId).toList();

        Map<String, Integer> votes = client.aggregateVotes(replyIDs);
        int highestVoteCount = votes.values().stream().mapToInt(Integer::valueOf).max().orElseThrow();
        System.out.println(votes);

        List<Integer> winningVotes = votes.entrySet()
                .stream()
                .filter(e -> e.getValue() == highestVoteCount)
                .map(Map.Entry::getKey)
                .map(Integer::parseInt)
                .toList();

        // Player Move
        int move = getRandom(winningVotes);
        game = game.move(move);

        // Bot Move
        int nextMove = getRandom(game.validMoves());
        game = game.move(nextMove);

        System.out.println(game);

        String id = client.post(game.toString());
        client.postVoteOptions(game.validMoves(), id);
    }

    public static void play() {
        Join4 game = new Join4(1);
        Scanner s = new Scanner(System.in);

        System.out.println(game);
        while (!game.isWin()) {
            System.out.print("> ");
            int move = s.nextInt();
            game = game.move(move);
            System.out.println(game);
        }
    }
}
