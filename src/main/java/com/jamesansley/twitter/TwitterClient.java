package com.jamesansley.twitter;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import com.twitter.clientlib.model.Get2TweetsIdResponse;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.model.TweetCreateResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TwitterClient {
    private final TwitterApi apiInstance;

    public TwitterClient() {
        TwitterCredentialsOAuth2 credentials = getCredentials();
        apiInstance = new TwitterApi(credentials);
    }

    public void post(String content) {
        TweetCreateRequest tweetRequest = new TweetCreateRequest().text(content);
        try {
            TweetCreateResponse result = apiInstance.tweets().createTweet(tweetRequest).execute();
            System.out.println(result);
        } catch (ApiException e) {
            printErr(e);
        }
    }

    public void getTweet(String id) {
        try {
            Get2TweetsIdResponse result = apiInstance.tweets().findTweetById(id).execute();
            System.out.println(result);
        } catch (ApiException e) {
            printErr(e);
        }
    }

    private TwitterCredentialsOAuth2 getCredentials() {
        Dotenv dotenv = Dotenv.load();
        TwitterCredentialsOAuth2 credentials = new TwitterCredentialsOAuth2(
                dotenv.get("CLIENT_ID"),
                dotenv.get("CLIENT_SECRET"),
                dotenv.get("ACCESS_TOKEN"),
                dotenv.get("REFRESH_TOKEN")
        );

        if (credentials.getTwitterOauth2AccessToken() == null) {
            OAuth2AccessToken accessToken = getAccessToken(credentials);
            if (accessToken == null) {
                throw new RuntimeException("Unable to get access token");
            }
            credentials.setTwitterOauth2AccessToken(accessToken.getAccessToken());
            credentials.setTwitterOauth2RefreshToken(accessToken.getRefreshToken());
        }
        return credentials;
    }

    public static OAuth2AccessToken getAccessToken(TwitterCredentialsOAuth2 credentials) {
        OAuth2AccessToken accessToken = null;
        try (TwitterOAuth20Service service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "https://127.0.0.1",
                String.join(" ", "tweet.read", "tweet.write", "users.read", "offline.access", "like.read")
        )) {
            final Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
            System.out.println("Fetching the Authorization URL...");

            final String secretState = "state";
            PKCE pkce = new PKCE();
            pkce.setCodeChallenge("challenge");
            pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
            pkce.setCodeVerifier("challenge");
            String authorizationUrl = service.getAuthorizationUrl(pkce, secretState);

            System.out.print(
                    "Go to the Authorization URL and authorize your App:\n"
                    + authorizationUrl
                    + "\nAfter that paste the authorization code here\n>> "
            );
            final String code = in.nextLine();
            System.out.println("\nTrading the Authorization Code for an Access Token...");
            accessToken = service.getAccessToken(pkce, code);

            System.out.println("ACCESS_TOKEN=" + accessToken.getAccessToken());
            System.out.println("REFRESH_TOKEN=" + accessToken.getRefreshToken());
        } catch (Exception e) {
            System.err.println("Error while getting the access token:\n " + e);
            e.printStackTrace();
        }
        return accessToken;
    }

    public static void printErr(ApiException e) {
        System.err.println("Exception when calling TweetsApi#createTweet");
        System.err.println("Status code: " + e.getCode());
        System.err.println("Reason: " + e.getResponseBody());
        System.err.println("Response headers: " + e.getResponseHeaders());
        e.printStackTrace();
    }
}
