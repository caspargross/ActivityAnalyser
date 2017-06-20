package com.vaadin.model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

/**
 * Created by caspar on 12.06.17.
 */
public class DbConnector extends MongoClient {

    MongoDatabase db;

    public DbConnector() {

        // Accesses DB, creates an Instance if it does not exist yet.
        this.db = getDatabase("trackFitDB");
    }

    public boolean findUser (String userName) {
        //db.getCollection("users")

        return true;
    }

    public void createUser(String googleUserData, GoogleCredential credential) {

        MongoCollection<Document> userColl = db.getCollection("users");
        Document userDoc = Document.parse(googleUserData);
        userDoc.append("refreshToken", credential.getRefreshToken());
        userDoc.append("serviceAccountUser", credential.getServiceAccountUser());
        userColl.insertOne(userDoc);
    }

    public String getUserImgURI(String userID) {
        return "TODO";

    }

    public String getUserRealName(String userID) {
        return "TODO";

    }



}
