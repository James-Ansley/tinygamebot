package com.jamesansley.twitter;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Get2TweetsSearchRecentResponse;
import com.twitter.clientlib.model.Tweet;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.model.TweetCreateResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jamesansley.twitter.TwitterAuth.getCredentials;
import static java.util.Comparator.comparing;

public class TwitterClient {
    private final TwitterApi apiInstance;

    public TwitterClient() throws ApiException {
        apiInstance = new TwitterApi(getCredentials());
        apiInstance.addCallback(new TwitterAuth.MaintainToken());
        apiInstance.refreshToken();
    }

    public String post(String content) throws ApiException {
        TweetCreateRequest tweetRequest = new TweetCreateRequest().text(content);
        TweetCreateResponse result = apiInstance.tweets().createTweet(tweetRequest).execute();
        return result.getData().getId();
    }

    public Tweet getLastPost() throws ApiException {
        Get2TweetsSearchRecentResponse result = apiInstance
                .tweets()
                .tweetsRecentSearch("from:tinygamebot")
                .maxResults(10)
                .tweetFields(Set.of("text", "created_at"))
                .execute();
        return result.getData().stream().max(comparing(Tweet::getCreatedAt)).orElseThrow();
    }

    public List<String> getReplies(String id) throws ApiException {
        String query = "in_reply_to_tweet_id:%s".formatted(id);
        Get2TweetsSearchRecentResponse result = apiInstance
                .tweets()
                .tweetsRecentSearch(query)
                .maxResults(100)
                .tweetFields(Set.of("text", "author_id"))
                .execute();
        List<String> uniqueReplies = new ArrayList<>();
        Set<String> userIDs = new HashSet<>();
        for (Tweet reply : result.getData()) {
            String replyID = reply.getAuthorId();
            if (!userIDs.contains(replyID)) {
                String text = reply.getText().replaceFirst("^@tinygamebot ", "");
                uniqueReplies.add(text);
            }
            userIDs.add(replyID);
        }
        return uniqueReplies;
    }

    public Tweet getTweet(String id) throws ApiException {
        return apiInstance.tweets()
                .findTweetById(id)
                .tweetFields(Set.of("public_metrics"))
                .execute()
                .getData();
    }
}
