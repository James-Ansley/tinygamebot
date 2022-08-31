package com.jamesansley.twitter;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.google.gson.Gson;
import com.twitter.clientlib.ApiClientCallback;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static java.util.stream.Collectors.joining;

public class TwitterAuth {
    private static final String tokenPath = "env/token.json";

    protected static OAuth2AccessToken getAccessToken(
            TwitterCredentialsOAuth2 credentials
    ) {
        OAuth2AccessToken accessToken = null;
        try (TwitterOAuth20Service service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "https://127.0.0.1",
                String.join(" ", "offline.access", "tweet.read", "tweet.write", "users.read", "like.read")
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
            System.out.println("Trading the Authorization Code for an Access Token...");
            accessToken = service.getAccessToken(pkce, code);
        } catch (Exception e) {
            System.err.println("Error while getting the access token:\n " + e);
            e.printStackTrace();
        }
        return accessToken;
    }

    protected static TwitterCredentialsOAuth2 getCredentials() {
        Dotenv dotenv = Dotenv.configure().directory("env").load();

        TwitterCredentialsOAuth2 credentials = new TwitterCredentialsOAuth2(
                dotenv.get("CLIENT_ID"), dotenv.get("CLIENT_SECRET"), null, null
        );

        File f = new File(tokenPath);
        OAuth2AccessToken accessToken;
        if (f.exists()) {
            accessToken = loadAccessToken();
        } else {
            accessToken = getAccessToken(credentials);
        }
        if (accessToken == null) {
            throw new RuntimeException("Unable to get access token");
        }
        credentials.setTwitterOauth2AccessToken(accessToken.getAccessToken());
        credentials.setTwitterOauth2RefreshToken(accessToken.getRefreshToken());

        return credentials;
    }

    protected static OAuth2AccessToken loadAccessToken() {
        try (FileReader f = new FileReader(tokenPath);
             BufferedReader reader = new BufferedReader(f)
        ) {
            String json = reader.lines().collect(joining("\n"));
            Gson gson = new Gson();
            return gson.fromJson(json, OAuth2AccessToken.class);
        } catch (IOException e) {
            throw new RuntimeException("Can't load file");
        }
    }

    public static class MaintainToken implements ApiClientCallback {
        @Override
        public void onAfterRefreshToken(OAuth2AccessToken accessToken) {
            Gson gson = new Gson();
            String json = gson.toJson(accessToken);
            try (
                    FileWriter file = new FileWriter(tokenPath);
                    BufferedWriter writer = new BufferedWriter(file)
            ) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
