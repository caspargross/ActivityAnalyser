package com.vaadin.model;


import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.vaadin.server.VaadinSession;
import jdk.nashorn.internal.parser.Token;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by caspar on 05.06.17.
 *
 * Requested Scope: https://www.googleapis.com/auth/fitness.activity.read
 */
public class MyAuthentification {

    /* Redirect URI */
    private static final String REDIRECT_URI = "http://localhost:8080";

    /* File path to secrets File */
    private static final String USER_SECRETS_FILE = "src/main/resources/client_secret.json";

    /* OAuth 2 scope. */
    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/fitness.activity.read",
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");

    /* Create instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /* Create instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /* File Credential Storage */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /* Authorization Flow */
    private GoogleAuthorizationCodeFlow flow;

    /* My Credential for this Session */
    private GoogleCredential myCredential;


    /* Standard Construtor, creates Authentification Flow */
    public MyAuthentification() {
        this.flow = initFlow();
    }


    public GoogleAuthorizationCodeFlow initFlow() {
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = null;
        try {
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT,
                    JSON_FACTORY,
                    loadClientSecrets(JSON_FACTORY),
                    SCOPES
            ).setAccessType("offline").setApprovalPrompt("force").build();
        } catch (IOException e) {
            System.out.println("Cannot read JSON with Client Secrets");
            e.printStackTrace();
        }

        return flow;
    }

    /* Load Google client secret (for this application) */
    private static GoogleClientSecrets loadClientSecrets(JsonFactory jsonFactory) throws IOException {
        return  GoogleClientSecrets.load(
                jsonFactory,
                new InputStreamReader(
                        new FileInputStream(USER_SECRETS_FILE), "UTF-8")

        );
    }

    public boolean authNeeded(String userName) {
        // TODO Check if access token is already in the database
        return true;
    }

    public String getAuthURI() {
        return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }

    public GoogleTokenResponse fetchToken(String code) throws IOException {
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        System.out.println("Token Response");
        System.out.println(tokenResponse.getIdToken());
        return tokenResponse;
                //flow.createAndStoreCredential(tokenResponse, "caspar.gross");
    }


    /* Getters and Setters */
    public static String getUserSecretsFile() {
        return USER_SECRETS_FILE;
    }

    public void obtainAuthCode(String code){
        System.out.println("Code obtained: ");
        System.out.println(code);
        try {
            TokenResponse myTokenResponse = fetchToken(code);
            myCredential = new GoogleCredential().setAccessToken(myTokenResponse.getAccessToken());
            myCredential.setRefreshToken(myTokenResponse.getRefreshToken());
            // STORE CREDENTIAL IN MONGODB
            updateUserData();
            //getFitData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUserData() throws IOException {
        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, myCredential)
                .setApplicationName("TrackFit").build();
        Userinfoplus userInfo = oauth2.userinfo().get().execute();
        System.out.println(userInfo.toPrettyString());
        DbConnector dbConnect = new DbConnector();
        dbConnect.storeUser(userInfo.toString(), myCredential);
        // Set Vaadin Session attribute to current user
        VaadinSession.getCurrent().setAttribute("userID", userInfo.getId());

    }

}
