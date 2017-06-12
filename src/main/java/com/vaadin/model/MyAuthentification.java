package com.vaadin.model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.fitness.Fitness;
import com.google.api.services.fitness.model.*;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;


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
    private Credential myCredential;

    private String USER_NAME;


    /* Google client secret (for this application) */
    private static GoogleClientSecrets loadClientSecrets(JsonFactory jsonFactory) throws IOException {
        return  GoogleClientSecrets.load(
                jsonFactory,
                new InputStreamReader(
                        new FileInputStream(USER_SECRETS_FILE), "UTF-8")

        );
    }

    /* Standard Construtor, creates Authentification Flow */
    public MyAuthentification(String userName) {
        this.USER_NAME = userName;
        this.flow = initFlow();
    }

    /* Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                loadClientSecrets(JSON_FACTORY),
                SCOPES
        ).build();

        // Check if already loaded:
        System.out.println(flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build());

        //flow.createAndStoreCredential()

        return null;
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
            ).build();
        } catch (IOException e) {
            System.out.println("Cannot read JSON with Client Secrets");
            e.printStackTrace();
        }
        return flow;
    }

    public boolean authNeeded(String userName) {
        // TODO Check if access token is already in the database
        return true;
    }

    public String getAuthURI() {
        return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }

    public Credential fetchCredential(String code) throws IOException {
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        System.out.println("Token Response");
        System.out.println(tokenResponse.getIdToken());
        return flow.createAndStoreCredential(tokenResponse, "caspar.gross");
    }


    /* Getters and Setters */
    public static String getUserSecretsFile() {
        return USER_SECRETS_FILE;
    }

    public void setCode(String code){
        System.out.println("Code obtained: ");
        System.out.println(code);
        try {
            myCredential = fetchCredential(code);
            // STORE CREDENTIAL IN MONGODB
            getUserData();
            getFitData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserData() throws IOException {
        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, myCredential)
                .setApplicationName("TrackFit").build();
        Userinfoplus userInfpo = oauth2.userinfo().get().execute();
        System.out.println(userInfpo.toPrettyString());
        DbConnector dbConnect = new DbConnector();
        dbConnect.createUser("caspar.gross", userInfpo.toString());
    }

    public void getFitData() throws IOException {

        Fitness fit = new Fitness.Builder(HTTP_TRANSPORT, JSON_FACTORY, myCredential)
                .setApplicationName("TrackFit").build();

        // Setting a start and end date using a range of 1 year before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTime = cal.getTimeInMillis();

        AggregateRequest aggRequest = new AggregateRequest();

        // set start- and endtime
        aggRequest.setStartTimeMillis(startTime);
        aggRequest.setEndTimeMillis(endTime);

        BucketByTime bucketByTime = new BucketByTime();
        bucketByTime.setDurationMillis(86400000l); // 24h
        aggRequest.setBucketByTime(bucketByTime);

        AggregateBy aggregateBy1 = new AggregateBy();
        aggregateBy1.setDataTypeName("com.google.step_count.delta");

        ArrayList<AggregateBy> list = new ArrayList<>();
        list.add(aggregateBy1);
        aggRequest.setAggregateBy(list);

        AggregateResponse aggResponse = fit.users().dataset()
                .aggregate("me", aggRequest).execute();

        System.out.println(aggResponse.toPrettyString());
    }
}
