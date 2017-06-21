package com.vaadin.model;


import com.google.api.client.auth.oauth2.Credential;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by caspar on 05.06.17.
 *
 * Requested Scope: https://www.googleapis.com/auth/fitness.activity.read
 */
public class AuthRequest extends ApiRequest{

    /* Authorization Flow */
    private GoogleAuthorizationCodeFlow flow;

    /* Standard Construtor, creates Authentification Flow */
    public AuthRequest() {
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


    public String getAuthURI() {
        return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }

    public Credential createCredential(String code) throws IOException {
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        System.out.println("Token Response");
        System.out.println(tokenResponse.getIdToken());
        return flow.createAndStoreCredential(tokenResponse, "id1");
    }


    public void handleReturnCode(String code){
        System.out.println("Code obtained: ");
        System.out.println(code);
        try {
            VaadinSession.getCurrent().setAttribute("sessionCredential", createCredential(code));
            myCredential = (Credential)VaadinSession.getCurrent().getAttribute("sessionCredential");
            
            // STORE CREDENTIAL IN MONGODB
            updateUserData();
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
