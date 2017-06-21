package com.vaadin.model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * Created by caspar on 12.06.17.
 */
public class DbConnector extends MongoClient {

    MongoDatabase db;
    static MongoCollection<Document> userColl;
    MongoCollection<Document> stepColl;

    Block<Document> printBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            System.out.println(document.toJson());
        }
    };

    public DbConnector() {

        // Accesses DB, creates an Instance if it does not exist yet.c
        this.db = getDatabase("trackFitDB");
        userColl = db.getCollection("users");
        stepColl = db.getCollection("steps");
    }

    public boolean findUser (String userName) {
        //db.getCollection("users")

        return true;
    }

    public void storeUser(String googleUserData, Credential credential) {

        Document userDoc = Document.parse(googleUserData);
        userDoc.append("refreshToken", credential.getRefreshToken());
        userColl.insertOne(userDoc);
    }

    public void storeSteps(String stepData) {
        Document stepDoc = Document.parse(stepData);
        stepColl.insertOne(stepDoc);

    }

    public static String extractUserPicture(String userID) {
        return userColl.find(eq("id", userID))
                .projection(fields(include("name", "picture"))).first().getString("picture");
    }

    public static String extractUserRealName(String userID) {
        return userColl.find(eq("id", userID))
                .projection(fields(include("name", "picture"))).first().getString("name");
    }



}
