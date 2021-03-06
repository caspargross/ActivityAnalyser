package com.vaadin.model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Created by caspar on 21.06.17.
 */
public class ApiRequest {

    /* Redirect URI */
    static final String REDIRECT_URI = "http://localhost:8080";

    /* File path to secrets File */
    static final String USER_SECRETS_FILE = "src/main/resources/client_secret.json";

    /* OAuth 2 scope. */
    static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/fitness.activity.read",
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");

    /* Create instance of the HTTP transport. */
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /* Create instance of the JSON factory. */
    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /* My Credential for this Session */
    Credential myCredential;

    /* Load Google client secret (for this application) */
    static GoogleClientSecrets loadClientSecrets(JsonFactory jsonFactory) throws IOException {
        return  GoogleClientSecrets.load(
                jsonFactory,
                new InputStreamReader(
                        new FileInputStream(USER_SECRETS_FILE), "UTF-8")

        );
    }
}
