package com.jamesansley.twitter;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;

import java.util.*;

import static com.jamesansley.twitter.TwitterAuth.getCredentials;

public class TwitterClient {
    private final TwitterApi apiInstance;
    private final TwitterApi helperApiInstance;

    public TwitterClient() throws ApiException {
        apiInstance = new TwitterApi(getCredentials("ACCESS_TOKEN", "REFRESH_TOKEN"));
        helperApiInstance = new TwitterApi(getCredentials("HELPER_ACCESS_TOKEN", "HELPER_REFRESH_TOKEN"));
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
                .tweetFields(Set.of("text"))
                .execute();
        return result.getData().get(0);
    }

    public List<Tweet> getHelperReplies(String id) throws ApiException {
        String query = "in_reply_to_tweet_id:%s from:tinygamebothelp".formatted(id);
        Get2TweetsSearchRecentResponse result = apiInstance
                .tweets()
                .tweetsRecentSearch(query)
                .tweetFields(Set.of("text"))
                .execute();
        return result.getData();
    }

    public String postReply(String content, String replyingTo) throws ApiException {
        TweetCreateRequest tweetRequest = new TweetCreateRequest()
                .text(content)
                .reply(new TweetCreateRequestReply()
                        .inReplyToTweetId(replyingTo));
        TweetCreateResponse result = helperApiInstance.tweets().createTweet(tweetRequest).execute();
        return result.getData().getId();
    }

    public void postVoteOptions(Collection<?> options, String replyingTo) throws ApiException {
        for (Object option : options) {
            postReply(option.toString(), replyingTo);
        }
    }

    public Get2TweetsIdResponse getTweet(String id) throws ApiException {
        return apiInstance.tweets()
                .findTweetById(id)
                .tweetFields(Set.of("public_metrics"))
                .execute();
    }

    public Map<String, Integer> aggregateVotes(Collection<String> ids) throws ApiException {
        Map<String, Integer> optionToWeight = new HashMap<>();
        for (String id : ids) {
            Get2TweetsIdResponse response = getTweet(id);
            Integer likes = response.getData().getPublicMetrics().getLikeCount();
            String option = response.getData().getText().replaceFirst("^@tinygamebot ", "");
            optionToWeight.put(option, likes);
        }
        return optionToWeight;
    }
}
